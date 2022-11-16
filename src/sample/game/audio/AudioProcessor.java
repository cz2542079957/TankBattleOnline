package sample.game.audio;

import java.io.File;
import java.applet.AudioClip;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;

//游戏音频处理器
public class AudioProcessor
{

    //音效文件预加载
    public static File gameStart = new File("src/resources/src/Audio/gameStart.wav");
    public static File fire = new File("src/resources/src/Audio/fire.wav");
    public static File hitWall = new File("src/resources/src/Audio/hitWall.wav");
    public static File hitHeavyTank = new File("src/resources/src/Audio/hitHeavyTank.wav");
    public static File hitAwardTank = new File("src/resources/src/Audio/hitAwardTank.wav");
    public static File enemyDestroy = new File("src/resources/src/Audio/enemyDestroy.wav");
    public static File playerDestroy = new File("src/resources/src/Audio/playerDestroy.wav");
    public static File getProps = new File("src/resources/src/Audio/getProps.wav");
    public static File getAddHPProps = new File("src/resources/src/Audio/getAddHPProps.wav");
    public static File getBombProps = new File("src/resources/src/Audio/getBombProps.wav");
    public static File gameOver = new File("src/resources/src/Audio/gameOver.wav");

    public static File playerMove = new File("src/resources/src/Audio/playerMove.wav");
    public static File playerStop = new File("src/resources/src/Audio/playerStop.wav");

//    public static MyAudioClip move = (MyAudioClip) createAudioClip(playerMove);
//    public static MyAudioClip stop = (MyAudioClip) createAudioClip(playerStop);

    public static AudioClip createAudioClip(final File audioFile)
    {
        try
        {
            //获取Clip对象
            final Clip clip = AudioSystem.getClip();
            //获取音乐输入流
            AudioInputStream ais = AudioSystem.getAudioInputStream(audioFile);
            //加载音乐
            clip.open(ais);
            //设置循环播放范围是整个音频
            clip.setLoopPoints(0, -1);
            //返回clip
            return new MyAudioClip(clip);
        } catch (UnsupportedAudioFileException e)
        {
            e.printStackTrace();
            System.err.println("系统音乐文件：" + audioFile.getAbsolutePath() + "的格式。");

        } catch (Exception e)
        {
            e.printStackTrace();
            System.err.println("加载音乐文件：" + audioFile.getAbsolutePath() + "时发送系统错误。");
        }

        return null;
    }

    public static void closeAudioClip(final AudioClip audioClip)
    {
        if (audioClip != null && audioClip instanceof MyAudioClip)
        {
            try
            {
                ((MyAudioClip) audioClip).clip.close();
            } catch (Exception e)
            {
            }
        }
    }

    public static void AudioPlayer(int type)
    {
        switch (type)
        {
            case 1:
                createAudioClip(gameStart).play();
                break;
            case 10:
                createAudioClip(fire).play();
                break;
            case 11:
                createAudioClip(hitWall).play();
                break;
            case 12:
                createAudioClip(hitHeavyTank).play();
                break;
            case 13:
                createAudioClip(hitAwardTank).play();
                break;
            case 14:
                createAudioClip(enemyDestroy).play();
                break;
            case 15:
                createAudioClip(playerDestroy).play();
                break;
            case 20:
                createAudioClip(getProps).play();
                break;
            case 21:
                createAudioClip(getAddHPProps).play();
                break;
            case 22:
                createAudioClip(getBombProps).play();
                break;
            case 2://游戏结束
                createAudioClip(gameOver).play();
                break;
        }

    }


}

