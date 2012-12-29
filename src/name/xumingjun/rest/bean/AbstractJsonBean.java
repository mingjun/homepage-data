package name.xumingjun.rest.bean;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class AbstractJsonBean {
	public final static Gson gson = new Gson();
	public String toJson(){
		return gson.toJson(this);
	}
	public String toJson(Gson gson){
		return gson.toJson(this);
	}

	public static <T> List<T> arrayFromJson(String array, Class<T> type) {
		JsonParser parser = new JsonParser();
		JsonArray Jarray = parser.parse(array).getAsJsonArray();
		ArrayList<T> list = new ArrayList<T>();
		for(JsonElement obj : Jarray ){
			T element = gson.fromJson( obj , type);
			list.add(element);
		}
		return list;
	}
}
