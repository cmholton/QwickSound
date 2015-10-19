package qwicksound;

import javax.sound.sampled.FloatControl;

/**
 * Represents a single instance of playback, which occurs when audio data is
 * actively being played. A {@code Playback} object is created and returned to
 * the caller every time one of the {@code play} methods of an {@code Audio}
 * object is called. A {@code Playback} object is initialized with the
 * attributes (volume, number of loops to be played) that were (optionally)
 * passed as arguments to a {@code play} method, and the {@code Playback} object
 * will thus represent an instance of playback with those attributes. A
 * {@code Playback} will run in its own thread and can be controlled (paused,
 * resumed, stopped, etc.) and have its mutable attributes (e.g. volume)
 * modified through its interface, independent of the {@code Audio} object that
 * created it.
 * <p>
 * There is a many-to-one relationship between {@code Playback} object(s) and
 * the {@code Audio} object that spawned them.
 *
 * @author Christian Holton
 * 
 */
public abstract class Playback {

	/**
	 * The {@code Audio} object associated with this {@code Playback}.
	 */
	protected Audio audio;
	/**
	 * The current state of this {@code Playback}.
	 */
	protected Playback.State state = Playback.State.PRE;
	/**
	 * Allows control of the volume over a range of floating-point values.
	 */
	protected FloatControl volCtrl;
	/**
	 * The value of the master-gain/volume for this {@code Playback}.
	 */
	protected double volume;
	/**
	 * The number of times the audio file will be played in succession.
	 */
	protected int numLoops;
	/**
	 * The instance number of this {@code Playback}. A {@code Playback} with an
	 * {@code instanceID} of i will be the ith {@code Playback} created by its
	 * associated {@code Audio}.
	 */
	protected long instanceID;

	/**
	 * Creates a new {@code Playback}.
	 * 
	 * @param audio
	 *            The {@code Audio} that created this {@code Playback}.
	 * @param instanceID
	 *            The {@code instanceID} of this {@code Playback}.
	 */
	protected Playback(Audio audio, long instanceID) {
		this.audio = audio;
		this.instanceID = instanceID;
	}

	/**
	 * Pause this {@code Playback}. To resume playback, {@code resume()} must be
	 * called.
	 */
	public abstract void pause();

	/**
	 * Resume play of this {@code Playback} once it has been paused. Play will
	 * resume at the last position played .
	 */
	public abstract void resume();

	/**
	 * Permanently stop this {@code Playback}. Once stopped, playback cannot be
	 * resumed.
	 */
	public abstract void stop();

	/**
	 * Get the current position of this {@code Playback} in seconds.
	 * 
	 * @return The current position in seconds.
	 */
	public abstract double getPosition();

	/**
	 * Get the number of consecutive times this {@code Playback} will play its
	 * associated {@code Audio}.
	 * 
	 * @return The number of loops for this {@code Playback}.
	 */
	public int getNumLoops() {
		return numLoops;
	}

	/**
	 * Get the volume for this {@code Playback}.
	 * 
	 * @return The volume of this {@code Playback}.
	 */
	public double getVolume() {
		return volume;
	}

	/**
	 * Set the volume for this {@code Playback}.
	 * 
	 * @param newVolume
	 *            The new volume for this {@code Playback}. Volume can range
	 *            from 0 (muted) to 2.0. The default volume is 1.0.
	 */
	public void setVolume(double newVolume) {
		newVolume = (newVolume < QwickSound.MIN_VOLUME ? QwickSound.MIN_VOLUME
				: newVolume);
		newVolume = (newVolume > QwickSound.MAX_VOLUME ? QwickSound.MAX_VOLUME
				: newVolume);
		volume = newVolume;
		float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
		if (volCtrl != null) {
			volCtrl.setValue(dB);
		}
	}

	/**
	 * Get the {@code Audio} associated with this {@code Playback}.
	 * 
	 * @return The associated {@code Audio}.
	 */
	public Audio getSound() {
		return audio;
	}

	/**
	 * Get the {@code instanceID} of this {@code Playback}.
	 * 
	 * @return The {@code instanceID}.
	 */
	public long getInstanceID() {
		return instanceID;
	}

	/**
	 * Get the current state of this {@code Playback}.
	 * 
	 * @return The {@code Playback.State} representing the state of this
	 *         {@code Playback}.
	 */
	public Playback.State getState() {
		return state;
	}

	/**
	 * The states that a {@code Playback} instance can be in.
	 */
	public enum State {
		/**
		 * The {@code Playback} object has been created but has not yet begun
		 * playback of audio.
		 */
		PRE,
		/**
		 * Playback of audio is currently taking place.
		 */
		PLAYING,
		/**
		 * Playback is currently paused.
		 */
		PAUSED,
		/**
		 * Playback has permanently stopped. This is a final, immutable state.
		 */
		STOPPED
	}
}
