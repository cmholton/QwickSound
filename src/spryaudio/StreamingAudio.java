package spryaudio;

import java.net.URL;

public class StreamingAudio extends Audio {

	private StreamingPlayback nextPlay;

	protected StreamingAudio(URL fileURL) {
		this.fileURL = fileURL;
		nextPlay = new StreamingPlayback(this,
				SpryAudio.acquireAudioInputStream(fileURL), numPlaybacks++);
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

	private StreamingPlayback startPlayback(double volume, int numLoops) {
		StreamingPlayback currentPlayback = nextPlay;
		currentPlayback.start(volume, numLoops, exec);

		// "Prep" for the next call to one of the play methods.
		nextPlay = new StreamingPlayback(this,
				SpryAudio.acquireAudioInputStream(fileURL), numPlaybacks++);
		return currentPlayback;
	}
}
