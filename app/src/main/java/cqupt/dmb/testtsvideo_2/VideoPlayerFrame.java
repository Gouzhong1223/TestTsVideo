package cqupt.dmb.testtsvideo_2;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description :
 * @Date : create by QingSong in 2022-04-06 13:13
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : cqupt.dmb.testtsvideo_2
 * @ProjectName : TestTSVideo-2
 * @Version : 1.0.0
 */
public class VideoPlayerFrame extends FrameLayout {
    private static final String TAG = "VideoPlayerFrame";

    private IMediaPlayer mIMediaPlayer = null;

    private FileDescriptor fileDescriptor;
    private SurfaceView mSurfaceView = null;

    private final Context mContext;

    private VideoPlayerListener mListener;

    public VideoPlayerFrame(Context context) {
        this(context, null);
    }

    public VideoPlayerFrame(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoPlayerFrame(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    /**
     */
    private void init() {
        setBackgroundColor(Color.BLACK);
        createSurfaceView();
        AudioManager mAudioManager = (AudioManager) mContext.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
    }

    private void createSurfaceView() {
        mSurfaceView = new SurfaceView(mContext);
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (mIMediaPlayer != null) {
                    mIMediaPlayer.setDisplay(holder);
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT
                , LayoutParams.MATCH_PARENT, Gravity.CENTER);
        addView(mSurfaceView, 0, layoutParams);
    }

    /**
     * 设置ijkplayer的监听
     */
    private void setListener(IMediaPlayer player) {
        player.setOnPreparedListener(mPreparedListener);
        player.setOnVideoSizeChangedListener(mVideoSizeChangedListener);
    }

    /**
     * 设置自己的player回调
     */
    public void setVideoListener(VideoPlayerListener listener) {
        mListener = listener;
    }

    /**
     * 设置播放地址
     *
     * @param path 视频源路径
     */
    public void setPath(String path) {
        setPath(path, null);
    }

    public void setPath(String path, Map<String, String> header) {
        /**
         * 视频文件路径
         */
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            fileDescriptor = fileInputStream.getFD();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /**
         * 视频请求header
         */
    }

    /**
     * 加载视频
     */
    public void load() throws IOException {
        if (mIMediaPlayer != null) {
            mIMediaPlayer.stop();
            mIMediaPlayer.release();
        }
        mIMediaPlayer = createPlayer();
        setListener(mIMediaPlayer);
        mIMediaPlayer.setDisplay(mSurfaceView.getHolder());
        mIMediaPlayer.setDataSource(fileDescriptor);

        mIMediaPlayer.prepareAsync();
    }

    /**
     * 开始播放视频
     */
    public void start() {
        if (mIMediaPlayer != null) {
            mIMediaPlayer.start();
        }
    }

    /**
     * 暂停播放视频
     */
    public void pause() {
        if (mIMediaPlayer != null) {
            mIMediaPlayer.pause();
        }
    }

    /**
     * 停止播放视频
     */
    public void stop() {
        if (mIMediaPlayer != null) {
            mIMediaPlayer.stop();
        }
    }

    /**
     * 重新播放视频
     */
    public void reset() {
        if (mIMediaPlayer != null) {
            mIMediaPlayer.reset();
        }
    }

    /**
     * 释放对象
     */

    public void release() {
        if (mIMediaPlayer != null) {
            mIMediaPlayer.reset();
            mIMediaPlayer.release();
            mIMediaPlayer = null;
        }
    }

    /**
     * 创建IMediaPlayer
     *
     * @return IMediaPlayer
     */
    private IMediaPlayer createPlayer() {
        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "http-detect-range-support", 1);

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "min-frames", 100);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);

        ijkMediaPlayer.setVolume(1.0f, 1.0f);

        /**
         * 是否硬解码
         */
        boolean mEnableMediaCodec = false;
        setEnableMediaCodec(ijkMediaPlayer, mEnableMediaCodec);
        return ijkMediaPlayer;
    }

    /**
     * 是否开启硬解码
     *
     * @param ijkMediaPlayer
     * @param isEnable
     */
    private void setEnableMediaCodec(IjkMediaPlayer ijkMediaPlayer, boolean isEnable) {
        int value = isEnable ? 1 : 0;
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", value);//开启硬解码
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", value);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", value);
    }


    private final IMediaPlayer.OnPreparedListener mPreparedListener = new IMediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            if (mListener != null) {
                mListener.onPrepared(iMediaPlayer);
            }
        }
    };

    private final IMediaPlayer.OnVideoSizeChangedListener mVideoSizeChangedListener = (iMediaPlayer, i, i1, i2, i3) -> {
        int videoWidth = iMediaPlayer.getVideoWidth();
        int videoHeight = iMediaPlayer.getVideoHeight();
        if (videoWidth != 0 && videoHeight != 0) {
            mSurfaceView.getHolder().setFixedSize(videoWidth, videoHeight);
        }
    };
}
