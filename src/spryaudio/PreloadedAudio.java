package spryaudio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public class PreloadedAudio extends Audio {
	

	private static final int BUFFER_SIZE = 4096;

	private AudioInputStream audioInStream;

	private AudioFormat audioFormat;

	private byte[] audioBytes;

	private PreloadedPlayback nextPlay;
	

	protected PreloadedAudio(URL fileURL) {
		this.fileURL = fileURL;
		audioInStream = SpryAudio.acquireAudioInputStream(fileURL);
		audioFormat = audioInStream.getFormat();
		loadDataAndPrep();
	}

	@Override
	public PreloadedPlayback play() {
		return startPlayback(SpryAudio.DEFAULT_VOLUME,
				SpryAudio.DEFAULT_NUM_LOOPS);
	}

	@Override
	public PreloadedPlayback play(double volume) {
		return startPlayback(volume, SpryAudio.DEFAULT_NUM_LOOPS);
	}

	@Override
	public PreloadedPlayback play(int numLoops) {
		return startPlayback(SpryAudio.DEFAULT_VOLUME, numLoops);
	}

	@Override
	public PreloadedPlayback play(double volume, int numLoops) {
		return startPlayback(volume, numLoops);
	}

	public void setPosition(double seconds) {
		nextPlay.setPosition(seconds);
	}

	public double getLength() {
		return nextPlay.getLength();
	}

	@Override
	public String toString() {
		return "PreloadedAudio " + getFileName();
	}

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

	private PreloadedPlayback startPlayback(double volume, int numLoops) {
		PreloadedPlayback currentPlay = nextPlay;
		currentPlay.start(volume, numLoops, exec);

		// "Prep" for the next call to one of the play methods.
		nextPlay = new PreloadedPlayback(this, audioFormat, audioBytes,
				++numPlaybacks);
		return currentPlay;
	}
}