package name.xumingjun.rest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import name.xumingjun.rest.bean.AbstractJsonBean;
import name.xumingjun.rest.bean.VisitInfo;
import name.xumingjun.util.CodeBeautifier;

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
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getGeneral() {
		//get cached hardware info, if possible
		if(null == hardwareInfo) {
			hardwareInfo = getHardware();
		}
		return new HostInfo(hardwareInfo, getUpTime()).toJson();
	}
	static final String SYSTEM_INFOMATION = "System Information";
	static final String SYSTEM_INFOMATION_REGEXP = "\\s+([-\\w ]+)\\: (.+)";
	static Map<String,String> hardwareInfo = null; // hardware info is barely changed
	public Map<String,String> getHardware() {
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
					Pattern pat = Pattern.compile(SYSTEM_INFOMATION_REGEXP);
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
			CodeBeautifier.close(in);
		}
		return hw;
	}
	public String getUpTime() {
		InputStream in = null;
		String uptime = null;
		try {
			ProcessBuilder pb = new ProcessBuilder("uptime");
			in = pb.start().getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			uptime = parseUpTime(br.readLine());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			CodeBeautifier.close(in);
		}
		return uptime;
	}
	static final String UP_TIME_REGEXP = ".+up\\s+(.+),\\s+\\d\\s+user.+";
	String parseUpTime(String raw) {
		Pattern pat = Pattern.compile(UP_TIME_REGEXP);
		Matcher m = pat.matcher(raw);
		return m.matches() ? m.group(1) : "unknown duration";
	}

	class HostInfo extends AbstractJsonBean {
		public Map<String, String> hardware;
		public String upTime;
		public HostInfo(Map<String, String> hw, String uptime) {
			this.hardware = hw;
			this.upTime = uptime;
		}
	}
	@GET
	@Path("/statistics")
	@Produces(MediaType.APPLICATION_JSON)
	public String getStatistics() {
		BufferedReader br = null;

		String result = "{}";
		try {
			br = new BufferedReader(new FileReader(VisitInfo.LOG_FILE));
			MonthlyStatisticsBuilder st = new MonthlyStatisticsBuilder();
			String line = null;
			while(null != (line = br.readLine())) {
				VisitInfo info = VisitInfo.parseLine(line);
				st.accumulate(info);
			}
			result = st.buildAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CodeBeautifier.close(br);
		}
		return result;
	}
}
class MonthlyStatistics extends AbstractJsonBean {
	List<AxisPoint> points;
	List<AxisLabel> xAxisLables;
}

class MonthlyStatisticsBuilder {
	private static class BaseTime {
		private static long time = 0; //2000.01.01 local time
		static {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 2000);
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			cal.set(Calendar.DATE, 1);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			time = cal.getTimeInMillis();
		}
		public static long getTime() {
			return time;
		}
	}
	public static long getBaseTime () {
		return BaseTime.getTime();
	}
	Map<Long, Set<String>> data4SingleIP = new HashMap<Long, Set<String>>();
	Calendar cal = Calendar.getInstance();
	public void accumulate(VisitInfo info) {
		cal.setTimeInMillis(info.timestamp);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		cal.setTimeInMillis(getBaseTime());
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		long key = cal.getTimeInMillis();
		Set<String> ips =  null;
		if(!data4SingleIP.containsKey(key)) {
			ips = new HashSet<String>();
			data4SingleIP.put(key, ips);
		} else {
			ips = data4SingleIP.get(key);
		}
		ips.add(info.remoteAddr);
	}
	private List<AxisPoint> buildAxisPoints4SingleIp() {
		cal.setTimeInMillis(getBaseTime());
		int baseYear = cal.get(Calendar.YEAR);
		int baseMonth = cal.get(Calendar.MONTH);
		Set<Long> keyset = data4SingleIP.keySet();
		ArrayList<AxisPoint> array = new ArrayList<AxisPoint>(keyset.size());
		for(Long key: keyset) {
			AxisPoint item = new AxisPoint();
			cal.setTimeInMillis(key);
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH);
			item.x = (year-baseYear)*12 + month-baseMonth;
			item.detail = data4SingleIP.get(key);
			item.y = item.detail.size();
			array.add(item);
		}
		Collections.sort(array);
		return array;
	}
	private List<AxisLabel> buildAxisLabels(List<AxisPoint> pointArray) {
		if(pointArray.size() <= 0) {
			return new ArrayList<AxisLabel>(0);
		}
		int min = pointArray.get(0).x;
		int max = pointArray.get(pointArray.size()-1).x;
		cal.setTimeInMillis(getBaseTime());
		cal.add(Calendar.MONTH, min);
		ArrayList<AxisLabel> array = new ArrayList<AxisLabel>(max-min+1);
		for(int i=min;i<=max;i++) {
			AxisLabel item = new AxisLabel();
			item.value = i;
			cal.add(Calendar.MONTH, 1);
			item.text = cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)-Calendar.JANUARY);
			array.add(item);
		}
		return array;
	}
	public String buildAll() {
		MonthlyStatistics o = new MonthlyStatistics();
		o.points = this.buildAxisPoints4SingleIp();
		o.xAxisLables = this.buildAxisLabels(o.points);
		return o.toJson();
	}
}

class AxisPoint implements Comparable<AxisPoint>{
	int x;
	int y;
	Set<String> detail;
	@Override
	public int compareTo(AxisPoint other) {
		return this.x - other.x;
	}
}

//class AxisPointWithDetail<DetailType> extends AxisPoint {
//	DetailType detail;
//}

class AxisLabel {
	int value;
	String text;
}

