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
import org.junit.Test;

public class TestHttpInvoke {
	private static final String HTTP_ATTACHMENT_URL="http://192.168.0.82:6691/attachment_provider";
	
	/**
	 * 文件上传
	 * 方法:fileSingleUpload
	 * 参数:userId:用户Id,这是特殊字段,传一个 ''字符串即可
	 * 参数:businessId:业务Id
	 * 参数:fileName:文件名
	 * 参数:fileSuffix:文件后缀
	 * @throws FileNotFoundException 
	 */
	@Test
	public void fileUpload() throws FileNotFoundException{
		FileInputStream is = new FileInputStream(new File("F:/resources/temp/temp3/temp_file/例子Demo列表 (1).xls"));
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("userId", "user1");
		params.put("businessId", "111111");
		params.put("fileSuffix", "xls");
		//文件名称在 sendSingleFile 这个方法的第三个参数中传递
		FileInfo fileInfo = HttpClientUtil.sendSingleFile(HTTP_ATTACHMENT_URL+"/httpFileInfo/fileSingleUpload", is, "例子Demo列表 (1)", params, FileInfo.class);
		System.out.println(fileInfo);
	}
	
	/**
	 * 通过业务Id查询文件
	 */
	@Test
	public void findFileInfoList(){
		List<FileInfo> findByBusinessId = findByBusinessId();
		System.out.println(findByBusinessId);
	}
	
	/**
	 * 通过业务Id查询文件
	 * 方法:findByBusinessId
	 * 参数:businessId:业务Id
	 */
	private List<FileInfo> findByBusinessId(){
		String url = HTTP_ATTACHMENT_URL+"/httpFileInfo/findByBusinessId";
		Map<String,String> params = new HashMap<String,String>();
		params.put("businessId", "111111");
		
		List<FileInfo> sendHttpGetList = HttpClientUtil.sendHttpGetList(url, params, FileInfo.class);
		return sendHttpGetList;
	}
	
	/**
	 * 通过文件信息Id查找文件具体信息
	 * 方法:findFileById
	 * 参数:id:文件信息Id
	 */
	@Test
	public void findFileById(){
		String url = HTTP_ATTACHMENT_URL+"/httpFileInfo/findFileById";
		
		List<FileInfo> fileInfoList = findByBusinessId();
		for (FileInfo fileInfo : fileInfoList) {
			Map<String,String> params = new HashMap<String,String>();
			params.put("id", fileInfo.getId());
			FileInfo singleFileInfo = HttpClientUtil.sendHttpGet(url, params,FileInfo.class);
			System.out.println(singleFileInfo);
		}
	}
	
	/**
	 * 删除文件
	 * 方法:deleteFiles
	 * 参数:ids :文件信息Id数组
	 * 注意:这里传参的map请用  IdentityHashMap ,该map运行多个相同的key同时存在
	 */
	@Test
	public void deleteFiles(){
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
	 * 方法:fileSingleDownloadStream
	 * 参数:id:文件信息Id
	 */
	@Test
	public void fileSingleDownloadStream(){
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
