package qwicksound;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import qwicksound.util.logging.LoggerConfig;

/**
 * {@code Audio} that loads it audio data completely into memory prior to being
 * played. {@code PreloadedAudio} can be compared to {@code StreamingAudio}
 * using the following attributes:
 * <p>
 * <ul>
 * <li>Functionality - Compared to {@code StreamingAudio},
 * {@code PreloadedAudio} offers extra functionality. Because
 * {@code PreloadedAudio} loads all its audio data into memory, the duration of
 * the audio can be determined (via the {@code getLength} method) and playback
 * can occur at an arbitrary position (via the {@code setPosition} method).</li>
 * <li>Memory - Compared to {@code PreloadedAudio}, {@code StreamingAudio} uses
 * less memory. {@code PreloadedAudio} requires memory equal to the size of its
 * audio file, while {@code StreamingAudio} only needs enough memory for its
 * read buffer.</li>
 * <li>Latency - {@code PreloadedAudio} will generally have less latency than
 * {@code StreamingAudio}. {@code PreloadedAudio} has all of its audio data in
 * memory and can therefore start playback immediately after a call to one of
 * the {@code play} methods, while {@code StreamingAudio} will need to wait for
 * its read buffer to be filled. In practice, any difference in latency is
 * rarely noticeable.</li>
 * </ul>
 * 
 * @author Christian Holton
 * 
 * @see PreloadedPlayback
 *
 */
public class PreloadedAudio extends Audio {

	/**
	 * The size of the input buffer that will temporarily hold the data read
	 * from the {@code AudioInputStream}.
	 */
	private static final int BUFFER_SIZE = 4096;
	/**
	 * An input stream with a specified audio format and length. The length is
	 * expressed in sample frames, not bytes.
	 */
	private AudioInputStream audioInStream;
	/**
	 * Specifies the particular arrangement of data in audioInStream.
	 */
	private AudioFormat audioFormat;
	/**
	 * An array of converted bytes from a {@code ByteArrayOutputStream}. This
	 * will hold the data that will be used by a {@code PreloadedPlayback} at
	 * each call to the {@code startPlayback} method.
	 */
	private byte[] audioBytes;
	/**
	 * When one of the {@code play} methods is called, there will be a small
	 * amount of latency that occurs before the playback of audio actually
	 * begins. In an effort to minimize this latency, a
	 * {@code PreloadedPlayback} object is "prepped" by creating it ahead of
	 * time, before it is needed, and saving it for the next call to a
	 * {@code play} method.
	 */
	private PreloadedPlayback nextPlay;
	/**
	 * {@code Logger} for the {@code PreloadedAudio} class.
	 */
	private static Logger logger = LoggerConfig.getLogger(PreloadedAudio.class
			.getName());

	/**
	 * Creates a new {@code PreloadedAudio} that is based on the audio file with
	 * the specified {@code URL}.
	 *
	 * @param fileURL
	 *            The audio file's {@code URL}.
	 */
	protected PreloadedAudio(URL fileURL) {
		this.fileURL = fileURL;
		audioInStream = QwickSound.acquireAudioInputStream(fileURL);
		audioFormat = audioInStream.getFormat();
		loadDataAndPrep();
	}

	@Override
	public PreloadedPlayback play() {
		return startPlayback(QwickSound.DEFAULT_VOLUME,
				QwickSound.DEFAULT_NUM_LOOPS);
	}

	@Override
	public PreloadedPlayback play(double volume) {
		return startPlayback(volume, QwickSound.DEFAULT_NUM_LOOPS);
	}

	@Override
	public PreloadedPlayback play(int numLoops) {
		return startPlayback(QwickSound.DEFAULT_VOLUME, numLoops);
	}

	@Override
	public PreloadedPlayback play(double volume, int numLoops) {
		return startPlayback(volume, numLoops);
	}

	/**
	 * Set the position that this {@code PreloadedAudio} will begin playback at
	 * once one of the {@code play} methods are called.
	 * 
	 * @param seconds
	 *            The desired position in seconds.
	 */
	public void setPosition(double seconds) {
		nextPlay.setPosition(seconds);
	}

	/**
	 * Get the length of this {@code PreloadedAudio} in seconds.
	 * 
	 * @return The length in seconds.
	 */
	public double getLength() {
		return nextPlay.getLength();
	}

	@Override
	public String toString() {
		return "PreloadedAudio " + getFileName();
	}

	/**
	 * Read the audio data from the {@code AudioInputStream} in chunks and write
	 * it to a {@code ByteArrayOutputStream} which will then be converted into a
	 * byte array.
	 *
	 * @return Success
	 */
	private boolean loadDataAndPrep() {
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		byte[] inBuffer = new byte[BUFFER_SIZE
				* audioInStream.getFormat().getFrameSize()];
		// Read all the data from the AudioInputStream in chunks until there
		// is no more to read.
		try {
			while (true) {
				int bytesRead = audioInStream.read(inBuffer);
				if (bytesRead == -1) {
					break;
				}
				byteOutStream.write(inBuffer, 0, bytesRead);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		} finally {
			try {
				// Release system resources.
				audioInStream.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		audioBytes = byteOutStream.toByteArray();
		// Prep for the initial playback.
		nextPlay = new PreloadedPlayback(this, audioFormat, audioBytes,
				++numPlaybacks);
		return true;
	}

	/**
	 * Call the current {@code PreloadedPlayback} to begin playback and create a
	 * {@code PreloadedPlayback} for the next playback. This method is called by
	 * all the {@code play} methods.
	 * 
	 * @param volume
	 *            The volume that playback will occur at.
	 * @param numLoops
	 *            The number of times the audio data will be played in
	 *            succession.
	 * 
	 * @return The {@code PreloadedPlayback} which is to be played.
	 */
	private PreloadedPlayback startPlayback(double volume, int numLoops) {
		PreloadedPlayback currentPlay = nextPlay;
		logger.info("Starting preloaded playback of \"" + getFileName()
				+ "\" instance " + currentPlay.getInstanceID());
		currentPlay.start(volume, numLoops, exec);

		// "Prep" for the next call to one of the play methods.
		nextPlay = new PreloadedPlayback(this, audioFormat, audioBytes,
				++numPlaybacks);
		return currentPlay;
	}
}