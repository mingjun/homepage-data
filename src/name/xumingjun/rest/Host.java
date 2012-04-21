package name.xumingjun.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import name.xumingjun.rest.bean.AbstractJsonBean;

@Path("/host")

public class Host {
	
//	Runtime rt = Runtime.getRuntime();
//	rt.exec("uname -a ").getInputStream();
	
//	ProcessBuilder pb = new ProcessBuilder("myshellScript.sh", "myArg1", "myArg2");
//	 Map<String, String> env = pb.environment();
//	 env.put("VAR1", "myValue");
//	 env.remove("OTHERVAR");
//	 env.put("VAR2", env.get("VAR1") + "suffix");
//	 pb.directory(new File("myDir"));
//	 Process p = pb.start();
	protected String generalJSON = null;
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getGeneral() {
		if(null == generalJSON) {
			generalJSON = about();
		}
		return generalJSON;
	}
	static final String SYSTEM_INFOMATION = "System Information";
	static final String INFO_REGEXP = "\\s+([-\\w ]+)\\: (.+)";
	
	public String about() {
		
		InputStream in = null;
		Map<String,String> hw = new HashMap<String,String>();
		try {
			ProcessBuilder pb = new ProcessBuilder("sudo", "dmidecode");
			in = pb.start().getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = null;
			boolean findStart = false;
			while(null != (line= br.readLine())) {
				if(!findStart){
					findStart = line.matches(SYSTEM_INFOMATION);
				} else {
					Pattern pat = Pattern.compile(INFO_REGEXP);
					Matcher m = pat.matcher(line);
					if(m.matches()) {
						hw.put(m.group(1), m.group(2));
					} else { // end
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(null != in) {
				try {
					in.close();
				} catch (IOException e) { }
			}
		}
		return new HostInfo(hw).toJson();
	}

	class HostInfo extends AbstractJsonBean {
		public Map<String, String> hardware;
		public HostInfo(Map<String, String> hw) {
			this.hardware = hw;
		}
	}
}
