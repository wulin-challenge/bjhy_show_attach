package org.apel.show.attach.test.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apel.gaia.commons.i18n.Message;
import org.apel.show.attach.service.domain.FileInfo;
import org.apel.show.attach.service.util.HttpClientUtil;

public class TestHttpInvoke {
	private static final String HTTP_ATTACHMENT_URL="http://192.168.0.198:6691/attachment_provider";
	
	public static void main(String[] args) throws FileNotFoundException {
		TestHttpInvoke testHttpInvoke = new TestHttpInvoke();
//		testHttpInvoke.fileUpload();
//		testHttpInvoke.findByBusinessId();
//		testHttpInvoke.deleteFiles();
		testHttpInvoke.fileSingleDownloadStream();
	}
	
	/**
	 * 文件上传
	 * @throws FileNotFoundException 
	 */
	private void fileUpload() throws FileNotFoundException{
		FileInputStream is = new FileInputStream(new File("F:/resources/temp/temp3/temp_file/例子Demo列表 (1).xls"));
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("userId", "user1");
		params.put("businessId", "businessId1");
		params.put("fileSuffix", "xls");
		
		FileInfo fileInfo = HttpClientUtil.sendSingleFile(HTTP_ATTACHMENT_URL+"/httpFileInfo/fileSingleUpload", is, "例子Demo列表 (1)", params, FileInfo.class);
		System.out.println(fileInfo);
	}
	
	/**
	 * 通过业务Id查询文件
	 */
	private List<FileInfo> findByBusinessId(){
		String url = HTTP_ATTACHMENT_URL+"/httpFileInfo/findByBusinessId";
		Map<String,String> params = new HashMap<String,String>();
		params.put("businessId", "businessId1");
		
		List<FileInfo> sendHttpGetList = HttpClientUtil.sendHttpGetList(url, params, FileInfo.class);
		return sendHttpGetList;
	}
	
	/**
	 * 删除文件
	 */
	private void deleteFiles(){
		List<FileInfo> fileInfoList = findByBusinessId();
		Map<String,String> params = new IdentityHashMap<String,String>();
		params.put(new String("ids"), fileInfoList.get(0).getId());
		params.put(new String("ids"), fileInfoList.get(1).getId());
		
		String url = HTTP_ATTACHMENT_URL+"/httpFileInfo/deleteFiles";
		Message sendHttpGet = HttpClientUtil.sendHttpGet(url, params, Message.class);
		System.out.println(sendHttpGet);
	}
	
	/**
	 * 下载单个文件
	 */
	private void fileSingleDownloadStream(){
		List<FileInfo> fileInfoList = findByBusinessId();
		FileInfo fileInfo = fileInfoList.get(0);
		
		Map<String,String> params = new IdentityHashMap<String,String>();
		params.put(new String("id"), fileInfoList.get(0).getId());
		
		String url = HTTP_ATTACHMENT_URL+"/httpFileInfo/fileSingleDownloadStream";
		
		HttpClientUtil.receiveSingleFile(url, params, new HttpClientUtil().new ReceiveSingleFileCallBack(){
			@Override
			public void fileStreamCallBack(long contentLength, InputStream fileStream) {
				String fileName = fileInfo.getFileName();
				String fileSuffix = fileInfo.getFileSuffix();
				
				try {
					FileUtils.copyToFile(fileStream, new File("F:/resources/temp/temp3/temp_file2/"+fileName+"."+fileSuffix));
					System.out.println();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
