package sample.game.audio;

import javax.sound.sampled.Clip;
import java.applet.AudioClip;

//自封装音频类
public class MyAudioClip implements AudioClip
{
    final public Clip clip;

    public MyAudioClip(final Clip clip)
    {

        this.clip = clip;

    }

    //播放
    public void play()
    {
        clip.setFramePosition(0);
        //把指针移到开始
        clip.start();
    }

    //循环播放
    public void loop()
    {
        clip.setFramePosition(0);
        //把指针移到开始
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop()
    {
        clip.stop();
    }

}
