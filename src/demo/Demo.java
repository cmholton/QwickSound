package demo;

import qwicksound.PreloadedAudio;
import qwicksound.PreloadedPlayback;
import qwicksound.QwickSound;
import qwicksound.StreamingAudio;
import qwicksound.StreamingPlayback;

/**
 * A short demonstration of how to use QwickSound.
 * 
 * 
 * @author Christian Holton
 *
 */
public class Demo {

	public static void main(String[] args) {

		// The system must be initialized.
		QwickSound.init();

		// Create the StreamingAudio instances. The files must be on the
		// classpath.
		StreamingAudio musicAudio = 
				QwickSound.createStreamingAudio("scifi_music.m4a");
		StreamingAudio helloAudio = 
				QwickSound.createStreamingAudio("golden_record_greeting.wav");

		// Create the PreloadedAudio instances. The files must be on the
		// classpath.
		PreloadedAudio saberAudio = 
				QwickSound.createPreloadedAudio("lightsaber.mp3");
		PreloadedAudio laserAudio = 
				QwickSound.createPreloadedAudio("laser_cannon.ogg");
		PreloadedAudio fireAudio = 
				QwickSound.createPreloadedAudio("video_game_fire.mp3");

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
		QwickSound.shutdown();
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

