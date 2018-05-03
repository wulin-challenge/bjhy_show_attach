package org.apel.show.attach.service.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * gsonUtil类
 * @author wubo
 *
 */
public class GsonUtil {
	
	public static final String START_ARRAY = "[";
	public static final String END_ARRAY = "]";
	
	/**
     * 得到返回类型实例的List数据(对应与一些特殊的字符可以正常转换)
     * @param data
     * @param clazz
     * @return
     */
	public static <T> List<T> getReturnTypeListEntity(String data,Class<T> clazz){
		//json字符串不能为空
		if(StringUtils.isBlank(data)) return null;
		//json字符串必须为数组节点类型
		if(!(data.startsWith(START_ARRAY) && data.endsWith(END_ARRAY))) return null;
		
		 //Json的解析类对象
	    JsonParser parser = new JsonParser();
	    //将JSON的String 转成一个JsonArray对象
	    JsonArray jsonArray = parser.parse(data).getAsJsonArray();
				
		List<T> returnList = new ArrayList<T>();
	    Gson gson = getGson();
	    
	  //加强for循环遍历JsonArray
	    for (JsonElement jsonObject : jsonArray) {
	        //使用GSON，直接转成Bean对象
	    	T perT = gson.fromJson(jsonObject, clazz);
	    	returnList.add(perT);
	    }
    	return returnList;
    }
	
	/**
     * 得到返回类型实例的List数据(使用 getReturnTypeListEntity替换)
     * 该方法不能处理一些特殊的字符,且T perT = gson.fromJson(object.toString(), clazz);中object.toString()转换出来
     * 的json也不是标准的,因此弃用
     * @param data
     * @param clazz
     * @return
     */
	@Deprecated
	public static <T> List<T> getReturnTypeListEntity2(String data,Class<T> clazz){
		//json字符串不能为空
		if(StringUtils.isBlank(data)) return null;
		//json字符串必须为数组节点类型
		if(!(data.startsWith(START_ARRAY) && data.endsWith(END_ARRAY))) return null;
				
		List<T> returnList = new ArrayList<T>();
	    Gson gson = getGson();
    	List<Object> fromJsonList = gson.fromJson(data, new TypeToken<List<T>>() {}.getType()); 
    	for (Object object : fromJsonList) {
    		T perT = gson.fromJson(object.toString(), clazz);
    		returnList.add(perT);
		}
    	return returnList;
    }
    
    /**
     * 得到返回类型实例的数据
     * @param data
     * @param clazz
     * @return
     */
    public static <T> T getReturnTypeEntity(String data,Class<T> clazz){
    	Gson gson = getGson();
    	return gson.fromJson(data, clazz);
    }
    
    /**
     * 得到 Gson
     * @return
     */
    public static Gson getGson(){
    	//解决时间转换的异常
		GsonBuilder builder = new GsonBuilder();
		
	    // Register an adapter to manage the date types as long values
	    builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
	    	
			@Override
			public Date deserialize(JsonElement json,java.lang.reflect.Type typeOfT,JsonDeserializationContext context) throws JsonParseException {
				 return new Date(json.getAsJsonPrimitive().getAsLong());
			}
	    });
	    Gson gson = builder
	    .setPrettyPrinting() //处理特殊字符
	    .disableHtmlEscaping()
	    .create();
	    return gson;
    }
    
    /** 
     * 对象转换成json字符串 
     * @param obj  
     * @return  
     */  
    public static String toJson(Object obj) {  
        Gson gson = getGson();
        return gson.toJson(obj);  
    }  

}