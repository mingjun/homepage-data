package name.xumingjun.util;

import java.io.Closeable;
import java.io.IOException;

public class CodeBeautifier {
	// close a stream, if available, often happens in finally clause.
	public static void closeStream(Closeable stream) {
		if (null != stream) {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// sleep current Thread
	public static void sleepThread(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
