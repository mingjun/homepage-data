package name.xumingjun.filter;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import name.xumingjun.rest.bean.VisitInfo;
import name.xumingjun.util.CodeBeautifier;

/**
 * Servlet Filter implementation class VisitFilter
 */
@WebFilter(description = "user visit", urlPatterns = { "/*" })
public class VisitFilter implements Filter {
	private PrintStream out = null;

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		out.println(new VisitInfo(System.currentTimeMillis(), req.getRemoteAddr(), req.getHeader("user-agent")));
		out.flush();
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		try {
			out = new PrintStream(new BufferedOutputStream(new FileOutputStream(VisitInfo.LOG_FILE, true)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		CodeBeautifier.closeStream(out);
		out = null;
	}

}
