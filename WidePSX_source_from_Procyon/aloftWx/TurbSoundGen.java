// 
// Decompiled by Procyon v0.5.36
// 

package aloftWx;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Line;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;
import java.io.File;

public class TurbSoundGen implements Runnable
{
    @Override
    public void run() {
        final String fileSeparator = new String(System.getProperty("file.separator"));
        File soundFile;
        if (TurbBase.getTurbSound().equals("L") || TurbBase.getTurbSound().equals("M")) {
            soundFile = new File("sound" + fileSeparator + "turb" + fileSeparator + "turbL.wav");
        }
        else {
            soundFile = new File("sound" + fileSeparator + "turb" + fileSeparator + "turbH.wav");
        }
        if (TurbBase.getSimPaused() == 0) {
            try {
                final AudioInputStream stream = AudioSystem.getAudioInputStream(soundFile);
                final AudioFormat format = stream.getFormat();
                final DataLine.Info info = new DataLine.Info(Clip.class, format);
                final Clip clip = (Clip)AudioSystem.getLine(info);
                clip.open(stream);
                clip.start();
                Thread.sleep(TurbBase.getTurbulenceSoundDuration() * 1000);
                clip.stop();
            }
            catch (Exception ex) {}
        }
    }
}
