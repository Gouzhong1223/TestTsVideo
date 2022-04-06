package cqupt.dmb.testtsvideo_2;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description :
 * @Date : create by QingSong in 2022-04-06 13:14
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : cqupt.dmb.testtsvideo_2
 * @ProjectName : TestTSVideo-2
 * @Version : 1.0.0
 */
public interface VideoPlayerListener extends IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnCompletionListener, IMediaPlayer.OnPreparedListener, IMediaPlayer.OnInfoListener, IMediaPlayer.OnVideoSizeChangedListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnSeekCompleteListener {
}
