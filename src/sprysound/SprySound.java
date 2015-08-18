package sprysound;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import sprysound.util.logging.LoggerConfig;

/**
 * Serves as the central class of the SprySound system, providing factory
 * methods that client code uses to create {@code Audio} instances.
 * Additionally, the system must be initialized and shutdown through the
 * {@code SprySound} interface.
 *
 * @author Christian Holton
 *
 */
public class SprySound {

	/**
	 * The minimum value for the master-gain/volume. This value will mute the
	 * volume.
	 */
	public static final double MIN_VOLUME = 0.0001;
	/**
	 * The maximum value for the master-gain/volume. This value will produce the
	 * loudest volume.
	 */
	public static final double MAX_VOLUME = 2.0;
	/**
	 * The default value of master-gain/volume used in absence of a
	 * user-supplied volume value.
	 */
	public static final double DEFAULT_VOLUME = 1.0;
	/**
	 * The default number of loops that sounds will be played in absence of a
	 * user-supplied number.
	 */
	public static final int DEFAULT_NUM_LOOPS = 1;
	/**
	 * {@code Logger} for the {@code SprySound} class.
	 */
	private static Logger logger = LoggerConfig.getLogger(SprySound.class
			.getName());

	/**
	 * Creates a new {@code PreloadedAudio} instance from the specified file
	 * name. The file associated with the new {@code PreloadedAudio} instance
	 * will be loaded completely into memory prior to being played. See
	 * {@link PreloadedAudio} for more information.
	 * <p>
	 * Note that the file's containing directory must be on the classpath.
	 * 
	 * @param fileName
	 *            The name of the audio file to load.
	 * 
	 * @return A new {@code PreloadedAudio} based on the specified file.
	 */
	public static PreloadedAudio createPreloadedAudio(String fileName) {
		return new PreloadedAudio(loadFile(fileName));
	}

	/**
	 * Creates a new {@code StreamingAudio} instance from the specified file
	 * name. The file associated with the new {@code StreamingAudio} instance
	 * will be streamed when played (i.e. read and played at the same time). See
	 * {@link StreamingAudio} for more information.
	 * <p>
	 * Note that the file's containing directory must be on the classpath.
	 * 
	 * @param fileName
	 *            The name of the audio file to load.
	 * 
	 * @return A new {@code StreamingAudio} based on the specified file
	 */
	public static StreamingAudio createStreamingAudio(String fileName) {
		return new StreamingAudio(loadFile(fileName));
	}

	/**
	 * Initialize the SprySound system.
	 */
	public static void init() {
		/*
		 * Currently, init() does not do anything. Future versions of SprySound,
		 * however, will need init() to be called, therefore the precedent must
		 * be set so that client code does not need to be modified in the
		 * future.
		 */
		logger.info("Initializing ...");
	}

	/**
	 * Shutdown the SprySound system in an orderly manner, allowing any
	 * currently playing sounds to finish before the system exits. More
	 * specifically, if there exists any {@code Playback} instance whose state
	 * equals {@code PlaybackState.PLAYING} or {@code PlaybackState.PAUSE} at
	 * the time {@code shutdown()} is called, then the system will wait until
	 * the states of all of those {@code Playback} instances have changed to
	 * {@code PlaybackState.STOP} before exiting.
	 */
	public static void shutdown() {
		logger.info("shutdown() called. Waiting for all playbacks "
				+ "instances to stop ...");
		Audio.shutdown();
	}

	/**
	 * Load the specified file.
	 * 
	 * NOTE: The file's containing directory must be on the classpath.
	 * 
	 * @param fileName
	 *            The name of the audio file to load. Only the name of the file
	 *            is needed - not the complete path to the file.
	 * 
	 * @return The {@code URL} of the file.
	 */
	private static URL loadFile(String fileName) {
		URL fileURL = ClassLoader.getSystemResource(fileName);
		if (fileURL == null) {
			logger.warning("Could not load file \"" + fileName + "\". Make"
					+ " sure the file exists and that it is on the classpath.");
		} else {
			logger.info("Loaded file \"" + fileName + "\"");
		}
		return fileURL;
	}

	/**
	 * Acquire an {@code AudioInputStream} based on the audio file data from the
	 * file {@code URL}. If the audio file is in MP3 or Ogg format, a call to
	 * {@code decodeToPCM} will be made and the {@code AudioInputStream} will be
	 * based on the new audio data.
	 * 
	 * @param fileURL
	 *            The specified file's URL.
	 * 
	 * @return The {@code AudioInputStream} based on the file's {@code URL}.
	 */
	protected static AudioInputStream acquireAudioInputStream(URL fileURL) {
		AudioInputStream audioInStream = null;
		String fileName = fileURL.getFile();
		try {
			// URL fileURL = ClassLoader.getSystemResource(fileName);
			audioInStream = AudioSystem.getAudioInputStream(fileURL);
		} catch (UnsupportedAudioFileException ex) {
			logger.warning("The audio format of the file \"" + fileName
					+ "\" could not be recognized.");
			ex.printStackTrace();
		} catch (IOException ex) {
			logger.warning("Could not aquire an AudioInputStream for the file \""
					+ fileName + "\"");
			ex.printStackTrace();
		}
		// If we have an encoded mp3 or ogg file, decode AudioInputStream to
		// PCM.
		if (fileName.endsWith(".mp3") || fileName.endsWith(".ogg")) {
			audioInStream = decodeToPCM(audioInStream);
		}
		return audioInStream;
	}

	/**
	 * Convert the MP3 or Ogg {@code AudioInputStream} to PCM. This method is
	 * the only code that pertains specifically to MP3 or Ogg files.
	 *
	 * From the {@code AudioInputStream}, i.e. from the audio file, we fetch
	 * information about the format of the audio data. This information includes
	 * the sampling frequency, the number of channels, and the size of the
	 * samples. This information is needed to ask Java Audio for a suitable
	 * output line for this {@code AudioInputStream}.
	 * 
	 * @param audioInStream
	 * @return The decoded, PCM-based {@code AudioInputStream}.
	 */
	private static AudioInputStream decodeToPCM(AudioInputStream audioInStream) {
		AudioFormat baseFormat = audioInStream.getFormat();
		// Create an AudioFormat with PCM encoding
		AudioFormat decodedFormat = new AudioFormat(
				AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(),
				16, baseFormat.getChannels(), baseFormat.getChannels() * 2,
				baseFormat.getSampleRate(), false);
		// Convert.
		audioInStream = AudioSystem.getAudioInputStream(decodedFormat,
				audioInStream);
		return audioInStream;
	}
}
