package name.xumingjun.util;

import java.io.IOException;
import java.io.InputStream;

public class Stream {
	// close a stream, if available, often happens in finally clause.
	public static void close(InputStream in) {
		if(null != in) {
			try {
				in.close();
			} catch (IOException e) { 
				e.printStackTrace();
			}
		}
	}

}
