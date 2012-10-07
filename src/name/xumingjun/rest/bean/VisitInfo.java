package name.xumingjun.rest.bean;

import name.xumingjun.util.ConfigConstants;
/**
 * Bean that describes a line of Visit log
 * @author mingjun
 *
 */
public class VisitInfo {
	public final static String LOG_FILE = ConfigConstants.VISIT_LOG_FILE;
	public final static String LOG_SEPERATER = "  |  ";
	public static String buildLine(VisitInfo info) {
		StringBuffer sb = new StringBuffer();
		sb.append(String.valueOf(info.timestamp))
			.append(LOG_SEPERATER)
			.append(info.remoteAddr)
			.append(LOG_SEPERATER)
			.append(info.userAgent);
		return sb.toString();
	}
	public static VisitInfo parseLine(String line) {
		final int len = LOG_SEPERATER.length();
		int a = line.indexOf(LOG_SEPERATER);
		int b = line.indexOf(LOG_SEPERATER, a+1);
		VisitInfo r;
		if(a>0 && b > a) {
			String t = line.substring(0, a);
			String addr = line.substring(a+len, b);
			String ua = line.substring(b+len, line.length());
			r = new VisitInfo(Long.parseLong(t), addr, ua);
		} else {
			r = new VisitInfo(0, "unknown", "unknown");
		}
		return r;
	}
	final public long timestamp;
	final public String remoteAddr;
	final public String userAgent;
	public VisitInfo(long  timestamp, String remoteAddr, String userAgent) {
		this.timestamp =  timestamp;
		this.remoteAddr = remoteAddr;
		this.userAgent = userAgent;
	}
	@Override
	public String toString() {
		return buildLine(this);
	}
}
