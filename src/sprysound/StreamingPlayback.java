package sprysound;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import sprysound.util.logging.LoggerConfig;

/**
 * A {@code Playback} that streams its audio data and is created by its
 * associated {@code StreamingAudio}.
 * 
 * @author Christian Holton
 * 
 * @see StreamingAudio
 * 
 */
public class StreamingPlayback extends Playback implements Runnable {

	/**
	 * The size of the input buffer that will temporarily hold data read from
	 * the {@code AudioInStream}.
	 */
	private static final int BUFFER_SIZE = 4096;
	/**
	 * An input stream with a specified audio format and length. The length is
	 * expressed in sample frames, not bytes.
	 */
	private AudioInputStream audioInStream;
	/**
	 * A {@code DataLine} that receives audio data for playback.
	 */
	private SourceDataLine line;
	/**
	 * A synchronization lock.
	 */
	private final ReentrantLock lock = new ReentrantLock();
	/**
	 * {@code Logger} for the {@code StreamingPlayback} class.
	 */
	private static Logger logger = LoggerConfig
			.getLogger(StreamingPlayback.class.getName());

	/**
	 * Creates a new {@code StreamingPlayback}. StreamingPlayback objects will
	 * always be created by their associated StreamingAudio object.
	 *
	 * @param audio
	 *            The {@code Audio} that created this {@code StreamingPlayback}.
	 * @param audioInStream
	 *            The {@code AudioInputStream} used by this
	 *            {@code StreamingPlayback}.
	 * @param instanceID
	 *            The {@code instanceID} of this {@code StreamingPlayback}.
	 */
	protected StreamingPlayback(Audio audio, AudioInputStream audioInStream,
			long instanceID) {

		super(audio, instanceID);
		this.audioInStream = audioInStream;

		AudioFormat audioFormat = audioInStream.getFormat();
		DataLine.Info info = new DataLine.Info(SourceDataLine.class,
				audioFormat);
		try {
			line = (SourceDataLine) AudioSystem.getLine(info);
			if (line != null) {
				line.open(audioFormat);
			}
			if (line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
				volCtrl = (FloatControl) line
						.getControl(FloatControl.Type.MASTER_GAIN);
			} else {
				logger.warning("Master-Gain control is not supported."
						+ " Volume will be fixed at the default level.");
			}
		} catch (LineUnavailableException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void pause() {
		if (line.isRunning() && getState() == Playback.State.PLAYING) {
			logger.info("Pausing playback of \"" + audio.getFileName()
					+ "\" instance " + instanceID);
			lock.lock();
			state = Playback.State.PAUSED;
		}
	}

	@Override
	public void resume() {
		if (!line.isRunning() && getState() == Playback.State.PAUSED) {
			logger.info("Resuming playback of \"" + audio.getFileName()
					+ "\" instance " + instanceID);
			lock.unlock();
			state = Playback.State.PLAYING;
		}
	}

	@Override
	public void stop() {
		logger.info("Stopping playback of \"" + audio.getFileName()
				+ "\" instance " + instanceID);
		line.stop();
		state = Playback.State.STOPPED;
		// Release system resources.
		line.close();
	}

	@Override
	public double getPosition() {
		return line.getMicrosecondPosition() / 1000000.0;
	}

	/**
	 * Run the thread that will effectively start playback of the audio data.
	 */
	@Override
	public void run() {
		setVolume(volume);
		int bytesRead;
		byte[] audioData = new byte[BUFFER_SIZE];
		line.start();

		BufferedInputStream bufferedIn = new BufferedInputStream(audioInStream);
		bufferedIn.mark(Integer.MAX_VALUE);

		try {
			for (int i = 0; i < numLoops; i++) {
				if (state == Playback.State.STOPPED) {
					break;
				}
				bytesRead = 0;
				while ((bytesRead = bufferedIn.read(audioData, 0,
						audioData.length)) != -1) {
					if (state == Playback.State.PAUSED) {
						if (line.isRunning()) {
							line.stop();
						}
						// Block.
						lock.lock();
						lock.unlock();
					}
					if (!line.isRunning()) {
						line.start();
					}
					// Playback.
					if (state != Playback.State.STOPPED) {
						state = Playback.State.PLAYING;
						line.write(audioData, 0, bytesRead);
					}
				}
				bufferedIn.reset();
			}
			// Important: drain() works like a flush on an output stream.
			line.drain();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			stop();
			try {
				bufferedIn.close();
				audioInStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String toString() {
		return "StreamingPlayback " + audio.getFileName() + " " + instanceID;
	}

	/**
	 * Start playback of this {@code StreamingPlayback}.
	 * 
	 * @param volume
	 *            The desired volume.
	 * @param numLoops
	 *            The number of times the audio data will be played in
	 *            succession.
	 * @param exec
	 *            Manages the thread that this {@code StreamingPlayback} runs
	 *            in.
	 */
	protected void start(double volume, int numLoops, final ExecutorService exec) {
		this.numLoops = (numLoops <= 0) ? Integer.MAX_VALUE : numLoops;
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
