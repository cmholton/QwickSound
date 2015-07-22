package spryaudio;

import javax.sound.sampled.FloatControl;

public abstract class Playback {

	protected Audio audio;

	protected Playback.State state = Playback.State.PRE;

	protected FloatControl volCtrl;

	protected double volume;

	protected int numLoops;

	protected long instanceID;
	

	protected Playback(Audio audio, long instanceID) {
		this.audio = audio;
		this.instanceID = instanceID;
	}

	public abstract void pause();

	public abstract void resume();

	public abstract void stop();

	public abstract double getPosition();

	public int getNumLoops() {
		return numLoops;
	}

	public double getVolume() {
		return volume;
	}

	public Audio getSound() {
		return audio;
	}

	public long getInstanceID() {
		return instanceID;
	}

	public Playback.State getState() {
		return state;
	}

	public enum State {

		PRE,

		PLAYING,

		PAUSED,

		STOPPED
	}
}
