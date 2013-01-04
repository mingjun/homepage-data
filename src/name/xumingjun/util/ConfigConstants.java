package name.xumingjun.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Properties;

/**
 * Centralize the configuration information
 * @author mingjun
 *
 */
public class ConfigConstants {
	public static String CONFIG_FILE = "web.properties";
	public static String HOST_PATH = "HOST_PATH";

	public static final String
		LOCAL_ROOT = getHostPath(),
		VISIT_LOG_FILE = LOCAL_ROOT + "/visit.log",
		SHARED_DIRECTORY = LOCAL_ROOT + "/share/",
		UPLOAD_PARENT_DIRECTORY = SHARED_DIRECTORY,
		SEARCH_INDEX_DIRECTORY = LOCAL_ROOT+"/../index"
		;

	static String getHostPath(){
		//".../WEB-INF/classes/"  -->  ".../WEB-INF/"
		String root = getRootPath()+"/../";
		Properties pps = new Properties();
		String hostPath;
		try {
			pps.load(new FileInputStream(new File(root, CONFIG_FILE)));
			hostPath = pps.getProperty(HOST_PATH);
		} catch (Exception e) {
			e.printStackTrace();
			hostPath = ".";
		}
		System.out.println(hostPath);
		return hostPath;
	}

	//normally for web application: ".../WEB-INF/classes"
	static String getRootPath() {
		String root = ConfigConstants.class.getResource("/").getPath();
		try {
			return  URLDecoder.decode(new File(root).getAbsolutePath(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return ".";
		}
	}
}
