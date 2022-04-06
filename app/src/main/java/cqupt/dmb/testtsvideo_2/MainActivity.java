package cqupt.dmb.testtsvideo_2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public class MainActivity extends AppCompatActivity implements VideoPlayerListener {

    private static final int WRITE_STORAGE_REQUEST_CODE = 100;

    private static final String TAG = "MainActivity";
    private VideoPlayerFrame mVideoPlayerFrame = null;

    @SuppressLint("SdCardPath")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context mContext = this;
        RequestPermissions(this);
        copyVideo();
        mVideoPlayerFrame = findViewById(R.id.videoPlayerFrame);
        mVideoPlayerFrame.setVideoListener(this);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mVideoPlayerFrame.setPath("/sdcard/video/霍元甲1.ts");
        try {
            mVideoPlayerFrame.load();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "播放失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onResume();
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
        Log.d(TAG, "onBufferingUpdate i = " + i);
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        Log.d(TAG, "onCompletion");
    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
        Log.d(TAG, "onError");
        mVideoPlayerFrame.stop();
        mVideoPlayerFrame.release();
        return false;
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        Log.d(TAG, "onPrepared");
        mVideoPlayerFrame.start();
    }

    @Override
    public void onSeekComplete(IMediaPlayer iMediaPlayer) {

    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_STORAGE_REQUEST_CODE) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "申请权限成功，打开");
            } else {
                Log.i(TAG, "申请权限失败");
            }
        }
    }

    private void RequestPermissions(@NonNull Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "没有授权，申请权限");
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, WRITE_STORAGE_REQUEST_CODE);
        } else {
            Log.i(TAG, "有权限，打开文件");
        }
    }

    @SuppressLint("SdCardPath")
    private void copyVideo() {
        new Thread(() -> {
            try {
                @SuppressLint("SdCardPath") FileInputStream fileInputStream = new FileInputStream("/sdcard/video/霍元甲.ts");
                FileOutputStream fileOutputStream = new FileOutputStream("/sdcard/video/霍元甲1.ts");
                byte[] bytes = new byte[188];
                while (readMpegTsPacket(fileInputStream, bytes)) {
                    fileOutputStream.write(bytes);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 从输入流中读取固定长度的数据
     *
     * @param inputStream 输入流
     * @param bytes       接收数组
     * @return 成功返回true, 失败返回false
     */
    public static boolean readMpegTsPacket(InputStream inputStream, byte[] bytes) {
        int nRead;
        try {
            bytes[0] = bytes[1] = bytes[2] = (byte) 0xff;
            while ((nRead = inputStream.read(bytes, 3, 1)) > 0) {
                if (bytes[0] == (byte) 0x47 && (bytes[1] == (byte) 0x40 || bytes[1] == (byte) 0x41 || bytes[1] == (byte) 0x50 || bytes[1] == (byte) 0x01) && (bytes[2] == (byte) 0x00 || bytes[2] == (byte) 0x11 || bytes[2] == 0x01)) {
                    break;
                }
                System.arraycopy(bytes, 1, bytes, 0, 3);
            }
            if (nRead <= 0) {
                return false;
            }
            /* 读取固定长度的字节 */
            int nLeft = 184;
            int pos = 4;
            while (nLeft > 0) {
                if ((nRead = inputStream.read(bytes, pos, nLeft)) <= 0) {
                    return false;
                }
                nLeft -= nRead;
                pos += nRead;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
