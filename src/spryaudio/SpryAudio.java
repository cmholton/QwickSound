package spryaudio;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SpryAudio {

	public static final double MIN_VOLUME = 0.0001;

	public static final double MAX_VOLUME = 2.0;

	public static final double DEFAULT_VOLUME = 1.0;

	public static final int DEFAULT_NUM_LOOPS = 1;

	public static void init() {
	}

	public static void shutdown() {
		Audio.shutdown();
	}

	private static URL loadFile(String fileName) {
		URL fileURL = ClassLoader.getSystemResource(fileName);
		return fileURL;
	}

	protected static AudioInputStream acquireAudioInputStream(URL fileURL) {
		AudioInputStream audioInStream = null;
		String fileName = fileURL.getFile();
		try {
			audioInStream = AudioSystem.getAudioInputStream(fileURL);
		} catch (UnsupportedAudioFileException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		// If we have an encoded mp3 or ogg file, decode AudioInputStream to PCM
		if (fileName.endsWith(".mp3") || fileName.endsWith(".ogg")) {
			audioInStream = decodeToPCM(audioInStream);
		}
		return audioInStream;
	}

	private static AudioInputStream decodeToPCM(AudioInputStream audioInStream) {
		AudioFormat baseFormat = audioInStream.getFormat();
		// Create an AudioFormat with PCM encoding
		AudioFormat decodedFormat = new AudioFormat(
				AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(),
				16, baseFormat.getChannels(), baseFormat.getChannels() * 2,
				baseFormat.getSampleRate(), false);
		// Convert
		audioInStream = AudioSystem.getAudioInputStream(decodedFormat,
				audioInStream);
		return audioInStream;
	}
}
