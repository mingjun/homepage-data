package name.xumingjun.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import name.xumingjun.rest.bean.AbstractJsonBean;
import name.xumingjun.util.CodeBeautifier;
import name.xumingjun.util.ConfigConstants;

/**
 * handle RESTful service for Showing Photos
 * @author mingjun
 *
 */
@Path("/photo")
public class Photo {

	public final static String LOCAL_ROOT = ConfigConstants.SHARED_DIRECTORY;
	public final static String WWW_ROOT = ConfigConstants.SHARED_DIRECTORY.replace(ConfigConstants.LOCAL_ROOT, "/");
	final static WeakHashMap<String, PhotoInfo> PHOTO_BY_URL = new WeakHashMap<String, PhotoInfo>();
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getPhotoList(@QueryParam("path") String path) {
		List<String> names = queryPhotoList(path);
		List<PhotoInfo> photos = new ArrayList<PhotoInfo>(names.size());
		for(String name: names) {
			PhotoInfo p = new PhotoInfo();
			p.url = name.replace(LOCAL_ROOT, WWW_ROOT);
			p.name = name.substring(name.lastIndexOf("/")+1);
			photos.add(p);
		}
		return AbstractJsonBean.gson.toJson(photos);
	}

	List<String> queryPhotoList(String path) {
		List<String> photoNames = new LinkedList<String>();
		InputStream in = null;
		try {
			ProcessBuilder pb = new ProcessBuilder("find", LOCAL_ROOT+path);
			in = pb.start().getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while(null != (line= br.readLine())) {
				if(line.matches(".+\\.(jpe?g|JPE?G|png|PNG)$"))
					photoNames.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			CodeBeautifier.finallyClose(in);
		}
		Collections.sort(photoNames);
		return photoNames;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/details")
	public String getPhotoWithDetails(@QueryParam("photoURLs") String photoURLs) {
		List<String> urls =  AbstractJsonBean.arrayFromJson(photoURLs, String.class);
		List<PhotoInfo> photos = new ArrayList<PhotoInfo>(urls.size());
		for(String url: urls) {
			photos.add(getPhotoInfo(url));
		}
		return AbstractJsonBean.gson.toJson(photos);
	}

	/**
	 * lazy load for photo detail info
	 * @param url
	 * @return
	 */
	PhotoInfo getPhotoInfo(String url) {
		PhotoInfo r;
		if(!PHOTO_BY_URL.containsKey(url)) {
			synchronized(PHOTO_BY_URL) {
				if(!PHOTO_BY_URL.containsKey(url)) {
					r = generatePhotoInfo(url);
					PHOTO_BY_URL.put(url, r);
				} else {
					r = PHOTO_BY_URL.get(url);
				}
			}
		} else {
			r = PHOTO_BY_URL.get(url);
		}
		return r;
	}

	PhotoInfo generatePhotoInfo(String url) {
		PhotoInfo info = new PhotoInfo();
		info.url = url;
		info.name = url.substring(url.lastIndexOf("/")+1);
		String localPath = info.url.replace(WWW_ROOT, LOCAL_ROOT);

		InputStream in = null;
		try {
			//based on server side GNU util: ImageMagick
			ProcessBuilder pb = new ProcessBuilder("identify", "-format", "%w*%h", localPath);
			in = pb.start().getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String size = br.readLine();
			int mid = size.indexOf('*');
			info.width = Integer.parseInt(size.substring(0, mid));
			info.height = Integer.parseInt(size.substring(mid+1));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			CodeBeautifier.finallyClose(in);
		}
		return info;
	}
}
class PhotoInfo {
	String url;
	String name;
	Integer width;
	Integer height;
}

