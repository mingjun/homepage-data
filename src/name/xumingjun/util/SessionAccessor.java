package name.xumingjun.util;

import javax.servlet.http.HttpSession;

public class SessionAccessor {
	@SuppressWarnings("unchecked")
	public synchronized static <T> T touchSessionAttribute(HttpSession session, String key, Class<T> type, T backup) {
		T r = null;
		Object attr = session.getAttribute(key);
		if(null != attr && type.isAssignableFrom(attr.getClass())) {
			r = (T)attr;
		} else { // not created
			session.setAttribute(key, backup);
			r = backup;
		}
//		System.out.println("session = "+session+ " r="+r);
		return r;
	}
}
