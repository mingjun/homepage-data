package name.xumingjun.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet Filter implementation class CrossDomainFilter
 */
@WebFilter(description = "x domain", urlPatterns = { "/*" })
public class CrossDomainFilter implements Filter {

    /**
     * Default constructor. 
     */
    public CrossDomainFilter() {
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if(response instanceof HttpServletResponse) {
			HttpServletResponse httpResponse = (HttpServletResponse)response;
			httpResponse.addHeader("Access-Control-Allow-Origin", "*");
			httpResponse.addHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE");
			httpResponse.addHeader("Access-Control-Allow-Headers", "origin, x-requested-with, content-type");
			httpResponse.addIntHeader("Access-Control-Max-Age", 1728000);
		}
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
	}

}
