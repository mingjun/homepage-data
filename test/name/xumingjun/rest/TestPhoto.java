package name.xumingjun.rest;


import java.util.ArrayList;
import java.util.List;

import name.xumingjun.rest.bean.AbstractJsonBean;

import org.junit.Test;

public class TestPhoto {
	Photo t = new Photo();
	@Test
	public void testQueryPhotoList() {
		System.out.println(t.queryPhotoList("wenxin"));
	}

	@Test
	public void testGetPhotoList() {
		System.out.println(t.getPhotoList("wenxin"));
	}
	@Test
	public void testGetPhotoSize() {
		String s = AbstractJsonBean.gson.toJson(t.generatePhotoInfo("/share/wenxin/7.JPG"));
		System.out.println(s);
	}

	@Test
	public void testGetPhotoWithDetails() {
		List<String> urls = new ArrayList<String>();
		urls.add("/share/wenxin/1.jpeg");
		urls.add("/share/wenxin/2.png");
		String photoURLs =  AbstractJsonBean.gson.toJson(urls);
		System.out.println(photoURLs);
		System.out.println(t.getPhotoWithDetails(photoURLs));
	}
}
