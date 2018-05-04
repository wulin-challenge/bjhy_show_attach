package org.apel.show.attach.provider.web;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.http.HttpEntity;
import org.apel.gaia.commons.i18n.Message;
import org.apel.show.attach.service.domain.FileInfo;
import org.apel.show.attach.service.service.FileInfoProviderService;
import org.apel.show.attach.service.util.HttpClientUtil;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.collect.Maps;

/**
 * 提供http调用的接口
 * @author wubo
 */
@RestController
@RequestMapping("httpFileInfo")
@CrossOrigin
public class HttpFileInfoController {
	
	@Reference(timeout=30000)
	private FileInfoProviderService fileInfoProviderService;
	
	/**
	 * 通过Id查找文件信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/findFileById", method = RequestMethod.GET)
	public @ResponseBody FileInfo findFileById(String id){
		return fileInfoProviderService.findFileById(id);
	}

	/**
	 * 通过业务Id查找出文件信息
	 * @param businessId
	 * @return
	 */
	@RequestMapping(value = "/findByBusinessId", method = RequestMethod.GET)
	public @ResponseBody List<FileInfo> findByBusinessId(String businessId){
		List<FileInfo> fileInfoList = fileInfoProviderService.findByBusinessId(businessId);
		return fileInfoList;
	}
	
	/**
	 * 批量删除
	 * @param ids 文件信息Id集合
	 * @return
	 */
	@RequestMapping(value="deleteFiles",method = RequestMethod.GET)
	public @ResponseBody Message batchDelete(String[] ids){
		for (String id : ids) {
			FileInfo fileInfo = fileInfoProviderService.findFileById(id);
			fileInfoProviderService.deleteFile(fileInfo);
			fileInfoProviderService.deleteById(id);
		}
		return new Message(0,"文件删除成功");
	}
	
	/**
	 * 文件单个上传
	 * @param request
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@RequestMapping(value = "fileSingleUpload",method = RequestMethod.POST)
	public FileInfo fileUpload(HttpServletRequest request){
		FileInfo fileInfo = null;
		try {
			String userId = request.getParameter("userId");
			String businessId = request.getParameter("businessId");
			String fileSuffix = request.getParameter("fileSuffix");
			
			//设置参数
			HashMap<String, String> param = Maps.newHashMap();
			param.put("fileSuffix", fileSuffix);
			param.put("userId", userId);
			param.put("businessId", businessId);

			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			if (!isMultipart) {
				return null;
			}
			ServletFileUpload upload = new ServletFileUpload();

			FileItemIterator iter = upload.getItemIterator(request);
			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				if (!item.isFormField()) {
					String fileName = item.getName();
					try (InputStream is = item.openStream();) {
						fileInfo = HttpClientUtil.syncSendSingleFile(CustomerFileInfoController.FILE_PROVIDER_URL_PREFIX+"/fileStore2", param, fileName, is, FileInfo.class); 
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileInfo;
	}
	
	/**
	 * 返回单个文件流
	 * @param request
	 */
	@SuppressWarnings("deprecation")
	@RequestMapping(value = "/fileSingleDownloadStream", method = RequestMethod.GET)
	public void returnFileStream(HttpServletRequest request,HttpServletResponse response) {
		String id = request.getParameter("id");
		FileInfo fileInfo = fileInfoProviderService.findFileById(id);
		
		//设置参数
		HashMap<String, String> param = Maps.newHashMap();
		param.put("relativePath", fileInfo.getRelativePath());
		
		String url = CustomerFileInfoController.FILE_PROVIDER_URL_PREFIX+"/returnFileStream";
		
		HttpEntity syncReceiveSingleFile = HttpClientUtil.syncReceiveSingleFile(url, param); 
		try (InputStream content = syncReceiveSingleFile.getContent()){
			long contentLength = syncReceiveSingleFile.getContentLength();
			
			HttpClientUtil.downloadSingleFile(content, (int)contentLength, param, request, response);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
