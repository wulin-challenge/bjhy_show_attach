package org.apel.show.attach.provider.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class HttpClientUtil {
	
	public static <T> T syncSendSingleFile(String url, Map<String, String> param, 
			String fileName, InputStream is, Class<T> responseClass){
//		Gson gson = new Gson();
		
		//解决时间转换的异常
		GsonBuilder builder = new GsonBuilder();

	    // Register an adapter to manage the date types as long values
	    builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
	    	
			@Override
			public Date deserialize(JsonElement json,
					java.lang.reflect.Type typeOfT,
					JsonDeserializationContext context)
					throws JsonParseException {
				 return new Date(json.getAsJsonPrimitive().getAsLong());
			}
	    });
	    Gson gson = builder.create();
		
		T result = null;
		try (CloseableHttpClient httpclient = HttpClients.createDefault();){
			MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
			HttpEntity data = multipartEntityBuilder
					.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
					.setCharset(Charsets.UTF_8)
					.addBinaryBody("upfile", is, ContentType.DEFAULT_BINARY,
							fileName).build();
			//popuate param
			List<BasicNameValuePair> params = param.keySet().stream().map(paramName -> {
				String value = param.get(paramName);
				return new BasicNameValuePair(paramName, value);
			}).collect(Collectors.toList());
			
			//send http  
			HttpUriRequest request = RequestBuilder
					.post(url)
					.setCharset(Charsets.UTF_8)
					.addParameters(Iterables.toArray(params, BasicNameValuePair.class))
					.setEntity(data).build();
			CloseableHttpResponse response = httpclient.execute(request);
			String responseString = EntityUtils.toString(response.getEntity());
			result = gson.fromJson(responseString, responseClass);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return result;
	}
	
	/**
	 * 异步接收单个文件
	 * @param url 要下载的文件url
	 * @param param 要传的参数
	 * @return
	 */
	@SuppressWarnings({ "deprecation", "resource" })
	public static HttpEntity syncReceiveSingleFile(String url, Map<String, String> param){
		 HttpClient client = new DefaultHttpClient();
         HttpGet get = new HttpGet(url);
         
         Set<Entry<String, String>> entrySet = param.entrySet();
         for (Entry<String, String> entry : entrySet) {
        	 get.addHeader(entry.getKey(), entry.getValue());
		}
         
         try {
			HttpResponse response=client.execute(get);
			 if(response.getStatusLine().getStatusCode()==200){
				 HttpEntity entity = response.getEntity();
				 return entity;
			 }
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}

}
