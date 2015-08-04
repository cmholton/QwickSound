package spryaudio;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Represents a single instance of audio, based on a audio file, that is to be
 * played. There is a one-to-one relationship between an {@code Audio} object
 * and its underlying audio file.
 * 
 * @author Christian Holton
 *
 */
public abstract class Audio {

	/**
	 * Manages the threads that are used to execute {@code Playback} objects.
	 */
	protected static ExecutorService exec = Executors.newCachedThreadPool();
	/**
	 * The {@code URL} of the audio file that is associated with this
	 * {@code Audio}.
	 */
	protected URL fileURL;
	/**
	 * The number of {@code Playbacks} that have been created by, and thus
	 * associated with, this {@code Audio}.
	 */
	protected long numPlaybacks;

	/**
	 * Play this {@code Audio} once at the default volume.
	 * 
	 * @return A new {@code Playback} instance that represents one play of this
	 *         {@code Audio} at the default volume.
	 */
	public abstract Playback play();

	/**
	 * Play this {@code Audio} once at the specified volume.
	 * 
	 * @param volume
	 *            The desired volume. Volume can range from 0 (muted) to 2.0.
	 *            The default volume is 1.0.
	 * 
	 * @return A new {@code Playback} instance that represents one play of this
	 *         {@code Audio} at the specified volume.
	 */
	public abstract Playback play(double volume);

	/**
	 * Play this {@code Audio} numLoops times at the default volume.
	 * 
	 * @param numLoops
	 *            The number of consecutive times this {@code Audio} will be
	 *            played. A value of -1 will loop forever.
	 * 
	 * @return A new {@code Playback} instance that represents numLoops
	 *         consecutive plays of this {@code Audio} at the default volume.
	 */
	public abstract Playback play(int numLoops);

	/**
	 * Play this {@code Audio} numLoops times at the specified volume.
	 * 
	 * @param volume
	 *            The desired volume. Volume can range from 0 (muted) to 2.0.
	 *            The default volume is 1.0.
	 * @param numLoops
	 *            The number of consecutive times this {@code Audio} will be
	 *            played. A value of -1 will loop forever.
	 * 
	 * @return A new {@code Playback} instance that represents numLoops
	 *         consecutive plays of this {@code Audio} at the specified volume.
	 */
	public abstract Playback play(double volume, int numLoops);

	/**
	 * Get the {@code URL} of the audio file associated with this {@code Audio}
	 * object.
	 * 
	 * @return The audio file's {@code URL}.
	 */
	public URL getFileURL() {
		return fileURL;
	}

	/**
	 * Get the filename and extension (not the complete path) of the audio file
	 * associated with this {@code Audio} object.
	 * 
	 * @return The audio file's name.
	 */
	public String getFileName() {
		String s = fileURL.getFile().substring(
				fileURL.getFile().lastIndexOf(File.separator) + 1);
		return s.replaceAll("%20", " ");
	}

	/**
	 * Get the number of {@code Playback} instances that are currently
	 * associated with this {@code Audio} object.
	 * 
	 * @return The number of {@code Playbacks}.
	 */
	public long getNumPlaybacks() {
		return numPlaybacks;
	}

	/**
	 * Shutdown the {@code Audio's ExecutorService} in an orderly manner,
	 * rejecting new {@code play} requests while allowing any currently playing
	 * instances of {@code Playback} to finish before the system exits. This
	 * method should only be called by the SpryAudio system's {@code shutdown}
	 * method.
	 */
	protected static void shutdown() {
		if (exec != null) {
			exec.shutdown();
		}
	}
}
