package name.xumingjun.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import name.xumingjun.rest.bean.AbstractJsonBean;
import name.xumingjun.search.FullTextSearcher;
import name.xumingjun.util.CodeBeautifier;
import name.xumingjun.util.ConfigConstants;
/**
 * handle search related RESTful calls
 *
 * @author mingjun
 */

@Path("/search")
public class Search {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String queryDocumentList(@QueryParam("clue") String clue) {
		if(CodeBeautifier.isBlank(clue)) {
			return "[]";
		}
		FullTextSearcher s = new FullTextSearcher(ConfigConstants.SEARCH_INDEX_DIRECTORY);
		return AbstractJsonBean.gson.toJson(s.query(clue.trim()));
	}
}
