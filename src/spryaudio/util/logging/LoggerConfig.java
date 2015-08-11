package spryaudio.util.logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Configures Java logging for SpryAudio.
 * 
 * @author Christian Holton
 *
 */
public class LoggerConfig {

	/**
	 * The current level of logging.
	 */
	public static final Level LEVEL = Level.INFO;

	/**
	 * Uncomment and use this logging level to turn off logging output.
	 */
	//public static final Level LEVEL = Level.OFF;

	/**
	 * Get the appropriate {@code Logger}.
	 * 
	 * @param name
	 *            The name of the {@code Logger}.
	 * 
	 * @return The {@code Logger} with the specified name.
	 */
	public static Logger getLogger(String name) {
		Logger logger = Logger.getLogger(name);
		for (Handler handler : logger.getParent().getHandlers()) {
			logger.getParent().removeHandler(handler);
		}
		Handler consoleHandler = new ConsoleHandler();
		consoleHandler.setFormatter(new CustomFormatter());
		logger.addHandler(consoleHandler);
		logger.setLevel(LEVEL);
		return logger;
	}
}

class CustomFormatter extends Formatter {

	private static final DateFormat df = new SimpleDateFormat(
			"MM/dd/yyyy hh:mm:ss");

	@Override
	public String format(LogRecord record) {
		return formatConcise(record);
	}

	private String formatConcise(LogRecord record) {
		StringBuilder sb = new StringBuilder();
		sb.append("SpryAudio: ");
		sb.append(record.getLevel());
		sb.append(" ");
		sb.append(record.getMessage());
		sb.append("\n");
		return sb.toString();
	}

	private String formatVerbose(LogRecord record) {
		StringBuilder sb = new StringBuilder(1000);
		sb.append(df.format(new Date(record.getMillis()))).append(" - ");
		sb.append("[").append(record.getSourceClassName()).append(".");
		sb.append(record.getSourceMethodName()).append("] - ");
		sb.append(formatConcise(record));
		return sb.toString();
	}
}
