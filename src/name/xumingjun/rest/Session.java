package name.xumingjun.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import name.xumingjun.rest.bean.AbstractJsonBean;
/**
 *
 * handle web session related RESTful calls
 * @author Mingjun
 *
 */
@Path("/session")
public class Session {

	@Context
	private HttpServletRequest request;
	/**
	 * get the current session id
	 * side-effect: set up sessionId as cookie to recognize session
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String GetUploadResult() {
		String sessionId = request.getSession().getId();
		return AbstractJsonBean.gson.toJson(sessionId);
	}

}
