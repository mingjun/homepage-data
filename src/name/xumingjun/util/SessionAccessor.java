package name.xumingjun.util;

import javax.servlet.http.HttpSession;

public class SessionAccessor {
	@SuppressWarnings("unchecked")
	public synchronized static <T> T touchSessionAttribute(HttpSession session, String key, Class<T> type, T backup) {
//		System.out.println("session = "+session);
		T r = null;
		Object attr = session.getAttribute(key);
		if(null != attr && attr.getClass().equals(type)) {
			r = (T)attr;
		} else { // not created
			session.setAttribute(key, backup);
			r = backup;
		}
		return r;
	}
}
