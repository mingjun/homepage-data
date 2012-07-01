package name;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import name.xumingjun.rest.Host;
import name.xumingjun.rest.WebFile;

import org.junit.Test;

public class testRunCmd {
	
	
	@Test
	public void testFile() {
		WebFile f = new WebFile();
		File ff = f.createPeerFile(WebFile.PARENCT_DIR, "hello.1.exe");
		System.out.println(ff.getAbsolutePath());
	}
	
	@Test
	public void testURL() throws UnsupportedEncodingException, URISyntaxException {
		String src = "0 a 1?";
		String code = URLEncoder.encode(src, "UTF-8");
		String back = URLDecoder.decode(code, "UTF-8");
		System.out.println(src+" -> "+code +" -> " +back);
		
		String back2 = URLDecoder.decode("0%20a%201", "UTF-8");
		System.out.println(back2);
		
		URI uri = new URI("http", "localhost", "/share/a b.pdf", null);
		System.out.println(uri.toString());
	}
}
