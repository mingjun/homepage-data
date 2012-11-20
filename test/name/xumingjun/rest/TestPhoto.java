package name.xumingjun.rest;


import java.util.ArrayList;
import java.util.List;

import name.xumingjun.rest.bean.AbstractJsonBean;

import org.junit.Test;

public class TestPhoto {
	Photo t = new Photo();
	@Test
	public void testQueryPhotoList() {
		System.out.println(t.queryPhotoList("pic"));
	}

	@Test
	public void testGetPhotoList() {
		System.out.println(t.getPhotoList("pic"));
	}
	@Test
	public void testGetPhotoSize() {
		String s = AbstractJsonBean.gson.toJson(t.generatePhotoInfo("/share/pic/e\u003dmc2.png"));
		System.out.println(s);
	}

	@Test
	public void testGetPhotoWithDetails() {
		List<String> urls = new ArrayList<String>();
		urls.add("/share/pic/e\u003dmc2.png");
		urls.add("/share/pic/code_html.jpeg");
		String photoURLs =  AbstractJsonBean.gson.toJson(urls);
		System.out.println(t.getPhotoWithDetails(photoURLs));
	}
}
