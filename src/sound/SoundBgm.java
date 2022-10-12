package sound;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.net.URLDecoder;
import java.util.logging.Logger;
import engine.Core;
import engine.FileManager;

public class SoundBgm {
    public Clip bgmClip;
    public File bgmFileLoader;
    protected Logger logger;

    public static enum SoundType{
        mainGameBgm,
        enemyKill,
        hit,
        menuClick,
        shoot
    }

    public SoundBgm(SoundType type){
        switch (type){
            case mainGameBgm:
                this.LoadBgm("BGM_MainGame_001.wav");
                break;
            case enemyKill:
                this.LoadBgm("EnemyKill_001.wav");
                break;
            case hit:
                this.LoadBgm("Hit_001.wav");
                break;
        }
    }

    public void LoadBgm(String filename){
        this.logger = Core.getLogger();
        try{
            String jarPath = FileManager.class.getProtectionDomain()
                    .getCodeSource().getLocation().getPath();
            jarPath = URLDecoder.decode(jarPath, "UTF-8");

            String soundPath = new File(jarPath).getParent();
            soundPath += File.separator;
            soundPath += "fri-space-invaders/" + filename;

            bgmFileLoader = new File(soundPath);
            AudioInputStream bgmInputStream = AudioSystem.getAudioInputStream(bgmFileLoader);
            bgmClip = AudioSystem.getClip();
            bgmClip.open(bgmInputStream);
            // 볼륨 설정용
//            FloatControl volumeControl = (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);
//            volumeControl.setValue(-10.0f);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public void play(){
        bgmClip.start();
    }

    public void setLoop(boolean isLoop){
        if(isLoop){
            bgmClip.loop(-1);
        } else {
            bgmClip.loop(1);
        }
    }
    public void stop(){ bgmClip.stop(); }

//    public void random(String fileNames[]){
//
//    }

}