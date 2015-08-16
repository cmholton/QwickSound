package demo;

import spryaudio.PreloadedAudio;
import spryaudio.PreloadedPlayback;
import spryaudio.SpryAudio;
import spryaudio.StreamingAudio;
import spryaudio.StreamingPlayback;

/**
 * A short demonstration of how to use SpryAudio.
 * 
 * 
 * @author Christian Holton
 *
 */
public class Demo {

	public static void main(String[] args) {

		// The system must be initialized.
		SpryAudio.init();

		// Create the StreamingAudio instances. The files must be on the
		// classpath.
		StreamingAudio musicAudio = 
				SpryAudio.createStreamingAudio("scifi_music.m4a");
		StreamingAudio helloAudio = 
				SpryAudio.createStreamingAudio("golden_record_greeting.wav");

		// Create the PreloadedAudio instances. The files must be on the
		// classpath.
		PreloadedAudio saberAudio = 
				SpryAudio.createPreloadedAudio("lightsaber.mp3");
		PreloadedAudio laserAudio = 
				SpryAudio.createPreloadedAudio("laser_cannon.ogg");
		PreloadedAudio fireAudio = 
				SpryAudio.createPreloadedAudio("video_game_fire.mp3");

		// Play the scifi music Audio and keep a reference to the returned
		// Playback so that we can control it later.
		StreamingPlayback musicPlayback = musicAudio.play();

		wait(3.0);

		// Play the hello Audio at a volume of 1.50.
		helloAudio.play(1.50);

		wait(4.0);

		// Set the position of the lightsaber Audio to 0.5 seconds.
		saberAudio.setPosition(0.5);

		// Play the lightsaber Audio at a volume of 1.25.
		saberAudio.play(1.25);

		wait(1.0);

		// Pause the music Playback.
		musicPlayback.pause();

		wait(1.0);

		// Play the laser Audio twice at a volume of 1.25.
		laserAudio.play(1.25, 2);
		
		// Adjust the volume of the music Playback.
		musicPlayback.setVolume(1.05);

		// Resume the music Playback.
		musicPlayback.resume();

		// Start the video game fire Audio and keep a reference to the returned
		// Playback so that we can control it later.
		PreloadedPlayback firePlayback = fireAudio.play();

		// Play the lightsaber Audio four times at a volume of 1.25.
		saberAudio.play(1.25, 4);

		// Set the position of the video game fire Playback to 0.5 seconds.
		firePlayback.setPosition(0.5);

		// Play the laser Audio at a volume of 1.25.
		laserAudio.play(1.25);

		wait(6.0);

		// Play the hello Audio at a volume of 1.50.
		helloAudio.play(1.50);

		// The system must be shutdown.
		SpryAudio.shutdown();
	}

	/**
	 * Make the calling Thread sleep. Used to simulate elapsing of time.
	 * 
	 * @param seconds
	 */
	public static void wait(double seconds) {
		try {
			Thread.sleep((int) (seconds * 1000));
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}
}

