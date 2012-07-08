package name.xumingjun.rest.bean;

public class VisitInfo {
	public final static String LOG_FILE = "/home/mingjun/www/visit.log";
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
		String t = line.substring(0, a);//FIXME
		String addr = line.substring(a+len, b);
		String ua = line.substring(b+len, line.length());
		return new VisitInfo(Long.parseLong(t), addr, ua);
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
