package gui;

import javax.sound.sampled.*;
import java.io.File;

/**
 * Přehrává krátké WAV zvuky s nastavitelnou hlasitostí.
 * Přehrávání je asynchronní (neblokuje EDT) — Clip si běží na vlastním vlákně.
 */
public class SoundPlayer {

    /**
     * @param filePath cesta k .wav souboru (relativní k pracovnímu adresáři, nebo absolutní)
     * @param volume   hlasitost 0.0 (ticho) až 1.0 (plná hlasitost)
     */
    public static void playWav(String filePath, float volume) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.err.println("Zvukový soubor nenalezen: " + file.getAbsolutePath());
                return;
            }

            AudioInputStream rawIn = AudioSystem.getAudioInputStream(file);
            AudioInputStream audioIn = toPlayableFormat(rawIn);

            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);

            applyVolume(clip, volume);

            // Uvolníme zdroje, jakmile zvuk doehraje, ať se Clipy nehromadí v paměti
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                    try {
                        audioIn.close();
                        rawIn.close();
                    } catch (Exception ignored) {}
                }
            });

            clip.start();

        } catch (Exception e) {
            System.err.println("Nepodařilo se přehrát zvuk: " + filePath);
            e.printStackTrace();
        }
    }

    /**
     * Java Sound (DirectAudioDevice) přímo podporuje jen 8/16-bit PCM.
     * Soubory exportované z DAW (FL Studio, Ableton apod.) bývají 24-bit nebo
     * 32-bit float — ty by na otevření Clipu spadly na LineUnavailableException.
     * Tady je proto vždy převedeme na bezpečný 16-bit PCM_SIGNED formát.
     */
    private static AudioInputStream toPlayableFormat(AudioInputStream source) {
        AudioFormat baseFormat = source.getFormat();

        AudioFormat targetFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                baseFormat.getSampleRate(),
                16,
                baseFormat.getChannels(),
                baseFormat.getChannels() * 2, // frame size = kanály × 2 byte (16-bit)
                baseFormat.getSampleRate(),
                false // little-endian
        );

        // Pokud je zdroj už v kompatibilním formátu, konverze je no-op (jen se vrátí ekvivalentní stream)
        return AudioSystem.getAudioInputStream(targetFormat, source);
    }

    /**
     * Nastaví hlasitost Clipu. volume je lineární 0.0–1.0, MASTER_GAIN control
     * ale pracuje v decibelech (logaritmická škála) — proto ten přepočet.
     */
    private static void applyVolume(Clip clip, float volume) {
        if (!clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) return;

        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

        // log10(0) je -nekonečno -> ošetříme minimální hodnotou, ať to nespadne při volume = 0
        float clamped = Math.max(0.0001f, Math.min(1f, volume));
        float dB = (float) (Math.log10(clamped) * 20.0);

        // Ořízneme na rozsah, který daný systém/zvukovka podporuje
        dB = Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), dB));

        gainControl.setValue(dB);
    }
}