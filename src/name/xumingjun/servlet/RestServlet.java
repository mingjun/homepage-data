package name.xumingjun.servlet;

import javax.servlet.annotation.WebServlet;

import com.sun.jersey.spi.container.servlet.ServletContainer;

/**
 * Servlet implementation class RestServlet
 */
@WebServlet(description = "Rest Servlet", urlPatterns = { "/*" })
public class RestServlet extends ServletContainer {
	private static final long serialVersionUID = 0L;
}
