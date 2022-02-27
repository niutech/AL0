package computer.fuji.al0.utils;

import android.media.AudioManager;
import android.media.ToneGenerator;

import java.util.Timer;
import java.util.TimerTask;

public class NumpadButtonTonePlayer {
    private static int tonePlayerMaxVolume = ToneGenerator.MAX_VOLUME / 100 * 80;
    private static int tonePlayerMinVolume = ToneGenerator.MIN_VOLUME;
    private static int tonePlayerVolume = tonePlayerMaxVolume;
    private static ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, tonePlayerVolume);
    private static Timer minimumTonePlayingDurationTimer;
    private static boolean minimumTonePlayingDurationIsPassed;
    private static boolean toneShouldBeStopInDelay;
    private static long MINIMUM_TONE_PLAYING_DURATION = 180;

    public static int volumeRangesToTonePlayerVolume (int min, int max, int volume) {
        int inputVolumeRange = max - min;
        int tonePlayerVolumeRange = tonePlayerMaxVolume - tonePlayerMinVolume;
        return ((volume - min) * tonePlayerVolumeRange / inputVolumeRange) + tonePlayerMinVolume;
    }

    private static void setTonePlayerVolume (int volume) {
        if (tonePlayerVolume != volume) {
            tonePlayerVolume = volume;
            toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, volume);
        }
    }

    public static void playTone (int dtmfTone, int volume) {
        setTonePlayerVolume(volume);
        // start DTMF tone
        toneGenerator.startTone(dtmfTone);
        // make sure tone is last at least MINIMUM_TONE_PLAYING_DURATION ms
        // reset minimum duration flag
        minimumTonePlayingDurationIsPassed = false;
        // reset should be stopped in delay flag
        toneShouldBeStopInDelay = false;
        // cancel previous timer
        if (minimumTonePlayingDurationTimer != null) {
            minimumTonePlayingDurationTimer.cancel();
        }

        minimumTonePlayingDurationTimer = new Timer();
        // scheule new timer after MINIMUM_TONE_PLAYING_DURATION ms
        minimumTonePlayingDurationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // update flag to make tone stoppable on stopDTMFTone function
                minimumTonePlayingDurationIsPassed = true;
                // stop tone if stopDTMFTone function get called before 250ms passed
                if (toneShouldBeStopInDelay) {
                    toneGenerator.stopTone();
                }
            }
        }, MINIMUM_TONE_PLAYING_DURATION);
    }

    public static void stopTone () {
        // check if at least MINIMUM_TONE_PLAYING_DURATION ms passed from tone start
        if (minimumTonePlayingDurationIsPassed) {
            toneGenerator.stopTone();
        } else {
            toneShouldBeStopInDelay = true;
        }
    }
}
