package name.xumingjun.rest.bean;

import com.google.gson.Gson;

public class AbstractJsonBean {
	public final static Gson gson = new Gson();
	public String toJson(){
		return gson.toJson(this);
	}
	public String toJson(Gson gson){
		return gson.toJson(this);
	}
}
