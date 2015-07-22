package spryaudio;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Audio {

	protected static ExecutorService exec = Executors.newCachedThreadPool();

	protected URL fileURL;

	protected long numPlaybacks;
	

	public abstract Playback play();

	public abstract Playback play(double volume);

	public abstract Playback play(int numLoops);

	public abstract Playback play(double volume, int numLoops);

	public URL getFileURL() {
		return fileURL;
	}

	public String getFileName() {
		return fileURL.getFile().substring(
				fileURL.getFile().lastIndexOf(File.separator) + 1);
	}

	public long getNumPlaybacks() {
		return numPlaybacks;
	}

	protected static void shutdown() {
		if (exec != null) {
			exec.shutdown();
		}
	}
}
