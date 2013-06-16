package name.xumingjun.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import name.xumingjun.jpa.bean.Photo;
import name.xumingjun.jpa.service.PersistenceService;
import name.xumingjun.jpa.service.ServiceManager;
import name.xumingjun.rest.bean.AbstractJsonBean;

/**
 * restful handler for wenxin's photos
 * @author mingjun
 *
 */
@Path("photo/wenxin")
public class WenXinPhoto {
	//path like http://www.xumingjun.cn/share/wenxin/DSC03477.JPG
	final static String PATH = "/share/wenxin/";

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllPhoto() {
		PersistenceService service = ServiceManager.instance().getPersistenceService();
		List<Photo> list = service.queryAll(Photo.class, "time");
		List<PhotoInfo> photos = new ArrayList<PhotoInfo>(list.size());
		for(Photo p: list) {
			PhotoInfo pi = new PhotoInfo(p);
			photos.add(pi);
		}
		return AbstractJsonBean.gson.toJson(photos);
	}

	// for generating json
	static class PhotoInfo {
		String url;
		String time;
		Integer width;
		Integer height;
		PhotoInfo(Photo jpaPhoto) {
			url = PATH + jpaPhoto.getName();
			time = jpaPhoto.getTime();
			String [] sizeParts = jpaPhoto.getSize().split("x");
			width = Integer.parseInt(sizeParts[0]);
			height = Integer.parseInt(sizeParts[1]);
		}
	}
}

