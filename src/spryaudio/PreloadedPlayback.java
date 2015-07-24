package spryaudio;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;

public class PreloadedPlayback extends Playback implements Runnable,
		LineListener {

	public Clip clip;

	private final Object lock = new Object();

	private boolean loopContinuously = false;

	protected PreloadedPlayback(Audio audio, AudioFormat audioFormat,
			byte[] audioBytes, long instanceID) {
		super(audio, instanceID);
		DataLine.Info info = new DataLine.Info(Clip.class, audioFormat);
		try {
			clip = (Clip) AudioSystem.getLine(info);
			clip.open(audioFormat, audioBytes, 0, audioBytes.length);
			if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
				volCtrl = (FloatControl) clip
						.getControl(FloatControl.Type.MASTER_GAIN);
			} else {

			}
		} catch (LineUnavailableException ex) {
			ex.printStackTrace();
		}
		clip.addLineListener(this);
	}

	@Override
	public void pause() {
		if (clip.isRunning() && getState() == Playback.State.PLAYING) {
			state = Playback.State.PAUSED; // This must be before clip.stop().
			clip.stop();
		}
	}

	@Override
	public void resume() {
		if (!clip.isRunning() && getState() == Playback.State.PAUSED) {
			long loopsPlayed = clip.getMicrosecondPosition()
					/ clip.getMicrosecondLength();
			int loopsToGo = (numLoops - (int) loopsPlayed);
			if (loopsToGo <= 0 && !loopContinuously) {
				loopsToGo = 1;
			} else if (loopContinuously) {
				loopsToGo = Clip.LOOP_CONTINUOUSLY;
			}
			state = Playback.State.PLAYING;
			clip.loop(loopsToGo);
		}
	}

	@Override
	public void stop() {
		state = Playback.State.STOPPED;
		clip.stop();
		clip.close();
	}

	@Override
	public double getPosition() {
		return clip.getMicrosecondPosition() / 1000000.0;
	}

	public void setPosition(double seconds) {
		if (seconds < 0) {
			seconds = 0;
		} else if (seconds > getLength()) {
			seconds = getLength() - 0.01;
		}
		clip.setMicrosecondPosition((long) (seconds * 1000000.0));
	}

	public double getLength() {
		return clip.getMicrosecondLength() / 1000000.0;
	}

	@Override
	public void run() {
		state = Playback.State.PLAYING;
		clip.loop(numLoops);
		// IMPORTANT: drain() works like a flush on an output stream.
		clip.drain();
		doWait();
	}

	@Override
	public String toString() {
		return "PreloadedPlayback " + audio.getFileName() + " " + instanceID;
	}

	private void doWait() {
		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void doNotify() {
		synchronized (lock) {
			lock.notify();
		}
	}

	@Override
	public void update(LineEvent event) {
		if (event.getType() == LineEvent.Type.CLOSE) {
			doNotify();
		}
		if ((event.getType() == LineEvent.Type.STOP)
				&& (state != Playback.State.PAUSED)) {
			// Only called when the clip is done playing its media.
			state = Playback.State.STOPPED;
			clip.stop();
			clip.close();
		}
	}

	protected void start(double volume, int numLoops, final ExecutorService exec) {
		this.numLoops = numLoops - 1; // By definition, loop(0) plays once.
		if (numLoops < 0) {
			this.numLoops = Clip.LOOP_CONTINUOUSLY; // (-1)
			loopContinuously = true;
		}
		setVolume(volume);
		try {
			exec.execute(this);
		} catch (RejectedExecutionException e) {
		}
	}
}