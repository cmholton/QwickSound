package spryaudio;

import java.net.URL;
import java.util.logging.Logger;

import spryaudio.util.logging.LoggerConfig;

/**
 * {@code Audio} that streams its audio data, defined as the audio data being
 * played while simultaneously read into memory. {@code StreamingAudio} can be
 * compared to {@code PreloadedAudio} using the following attributes:
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
 * @see StreamingPlayback
 *
 */
public class StreamingAudio extends Audio {

	/**
	 * When one of the {@code play} methods is called, there will be a small
	 * amount of latency that occurs before the playback of audio actually
	 * begins. In an effort to minimize this latency, a
	 * {@code StreamingPlayback} object is "prepped" by creating it ahead of
	 * time, before it is needed, and saving it for the next call to a
	 * {@code play} method.
	 */
	private StreamingPlayback nextPlay;
	/**
	 * {@code Logger} for the {@code StreamingAudio} class.
	 */
	private static Logger logger = LoggerConfig.getLogger(StreamingAudio.class
			.getName());

	/**
	 * Creates a new {@code StreamingAudio} that is based on the audio file with
	 * the specified {@code URL}.
	 *
	 * @param fileURL
	 *            The audio file's {@code URL}.
	 */
	protected StreamingAudio(URL fileURL) {
		this.fileURL = fileURL;
		nextPlay = new StreamingPlayback(this,
				SpryAudio.acquireAudioInputStream(fileURL), ++numPlaybacks);
	}

	@Override
	public StreamingPlayback play() {
		return startPlayback(SpryAudio.DEFAULT_VOLUME,
				SpryAudio.DEFAULT_NUM_LOOPS);
	}

	@Override
	public StreamingPlayback play(double volume) {
		return startPlayback(volume, SpryAudio.DEFAULT_NUM_LOOPS);
	}

	@Override
	public StreamingPlayback play(int numLoops) {
		return startPlayback(SpryAudio.DEFAULT_VOLUME, numLoops);
	}

	@Override
	public StreamingPlayback play(double volume, int numLoops) {
		return startPlayback(volume, numLoops);
	}

	@Override
	public String toString() {
		return "StreamingAudio " + getFileName();
	}

	/**
	 * Call the current {@code StreamingPlayback} to begin playback and create a
	 * {@code StreamingPlayback} for the next playback. This method is called by
	 * all the {@code play()} methods.
	 *
	 * @param volume
	 *            The volume that playback will occur at.
	 * @param numLoops
	 *            The number of times the audio data will be played in
	 *            succession.
	 * 
	 * @return The {@code StreamingPlayback} which is to be played.
	 */
	private StreamingPlayback startPlayback(double volume, int numLoops) {
		StreamingPlayback currentPlayback = nextPlay;
		logger.info("Starting streaming playback of \"" + getFileName()
				+ "\" instance " + currentPlayback.getInstanceID());
		currentPlayback.start(volume, numLoops, exec);

		// "Prep" for the next call to one of the play methods.
		nextPlay = new StreamingPlayback(this,
				SpryAudio.acquireAudioInputStream(fileURL), ++numPlaybacks);
		return currentPlayback;
	}
}
