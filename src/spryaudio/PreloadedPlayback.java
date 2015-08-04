package spryaudio;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;

import spryaudio.util.logging.LoggerConfig;

/**
 * A {@code Playback} that preloads its audio data and is created by its
 * associated {@code PreloadedAudio}.
 * 
 * @author Christian Holton
 * 
 * @see PreloadedAudio
 * 
 */
public class PreloadedPlayback extends Playback implements Runnable,
		LineListener {

	/**
	 * Holds the preloaded audio data.
	 */
	public Clip clip;
	/**
	 * Used as a synchronization lock.
	 */
	private final Object lock = new Object();
	/**
	 * Flag for continuous looping.
	 */
	private boolean loopContinuously = false;
	/**
	 * {@code Logger} for the {@code PreloadedPlayback} class.
	 */
	private static Logger logger = LoggerConfig
			.getLogger(PreloadedPlayback.class.getName());

	/**
	 * Creates a new {@code PreloadedPlayback}. PreloadedPlayback objects will
	 * always be created by their associated PreloadedAudio object.
	 * <p>
	 * IMPLEMENTATION NOTE: Originally, the fetching of a new {@code Line} was
	 * done in the {@code run} method, however testing revealed that latency is
	 * decreased if a {@code Line} is acquired ahead of time, here in the
	 * constructor.
	 * 
	 * @param audio
	 *            The {@code Audio} that created this {@code PreloadedPlayback}.
	 * @param audioFormat
	 *            Specifies the particular arrangement of audio data.
	 * @param audioBytes
	 *            Holds the audio data from which a {@code Clip} will be
	 *            created.
	 * @param instanceID
	 *            The {@code instanceID} of this {@code PreloadedPlayback}.
	 */
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
				logger.warning("Master-Gain control is not supported."
						+ " Volume will be fixed at the default level.");
			}
		} catch (LineUnavailableException ex) {
			ex.printStackTrace();
		}
		clip.addLineListener(this);
	}

	@Override
	public void pause() {
		if (clip.isRunning() && getState() == Playback.State.PLAYING) {
			logger.info("Pausing playback of \"" + audio.getFileName()
					+ "\" instance " + instanceID);
			state = Playback.State.PAUSED; // This must be before clip.stop().
			clip.stop();
		}
	}

	@Override
	public void resume() {
		if (!clip.isRunning() && getState() == Playback.State.PAUSED) {
			logger.info("Resuming playback of \"" + audio.getFileName()
					+ "\" instance " + instanceID);
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
		logger.info("Stopping playback of \"" + audio.getFileName()
				+ "\" instance " + instanceID);
		state = Playback.State.STOPPED;
		clip.stop();
		clip.close();
	}

	@Override
	public double getPosition() {
		return clip.getMicrosecondPosition() / 1000000.0;
	}

	/**
	 * Set the position of this {@code PreloadedPlayback} in seconds.
	 * 
	 * @param seconds
	 *            The desired position in seconds.
	 */
	public void setPosition(double seconds) {
		if (seconds < 0) {
			seconds = 0;
		} else if (seconds > getLength()) {
			seconds = getLength() - 0.01;
		}
		clip.setMicrosecondPosition((long) (seconds * 1000000.0));
	}

	/**
	 * Get the length of this {@code PreloadedPlayback} in seconds.
	 * 
	 * @return The length in seconds.
	 */
	public double getLength() {
		return clip.getMicrosecondLength() / 1000000.0;
	}

	/**
	 * Run the thread that will effectively initiate playback of the audio data.
	 */
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

	/**
	 * Make the current thread wait for the audio data to finish playing. This
	 * is necessary because {@code clip.loop()} starts its own daemon thread
	 * (when called in the {@code run} method above) and the current thread (the
	 * thread that {@code PreloadedPlayback} is running in) will return. The
	 * current thread is managed by an {@code Executor} and if
	 * {@code Executor.shutdown()} has been called, and the current thread has
	 * returned, the {@code clip.loop()} thread will not stop the
	 * {@code Executor} from shutting down, and not stop the SpryAudio system
	 * from exiting, therefore prematurely terminating the {@code clip.loop()}
	 * playback. To remedy this, the current thread must be forced to wait until
	 * it is notified that the {@code Clip} is closed. This method will be
	 * called when the {@code update()} method receives a {@code LineEvent} of
	 * type CLOSE.
	 * 
	 */
	private void doWait() {
		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Notify the current thread to stop waiting.This method will be called when
	 * the {@code clip.loop()} thread (see the {@code doWait} method for more
	 * info) has finished.
	 */
	private void doNotify() {
		synchronized (lock) {
			lock.notify();
		}
	}

	/**
	 * Implements the {@code LineListener} interface. This method is called
	 * whenever the {@code Clip} has a line event.
	 *
	 * @param event
	 *            Encapsulates the line event information sent from the
	 *            {@code Clip}.
	 */
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

	/**
	 * Start playback of this {@code PreloadedPlayback}.
	 * 
	 * @param volume
	 *            The desired volume.
	 * @param numLoops
	 *            The number of times the audio data will be played in
	 *            succession.
	 * @param exec
	 *            Manages the thread that this {@code PreloadedPlayback} runs
	 *            in.
	 */
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
			logger.warning("A play request was received "
					+ "but the system is shutting down."
					+ " Cannot perform the play request.");
		}
	}
}