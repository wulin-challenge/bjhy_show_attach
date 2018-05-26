package org.apel.show.attach.service.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apel.gaia.commons.i18n.Message;
import org.apel.gaia.commons.jqgrid.QueryParams;
import org.apel.gaia.commons.pager.PageBean;
import org.apel.gaia.util.BeanUtils;
import org.apel.gaia.util.jqgrid.JqGridUtil;
import org.apel.show.attach.service.domain.FileInfo;
import org.apel.show.attach.service.service.FileInfoProviderService;
import org.apel.show.attach.service.util.FileUtil;
import org.apel.show.attach.service.util.HttpClientUtil;
import org.apel.show.attach.service.util.UnZipStorePath;
import org.apel.show.attach.service.util.UnZipStorePath.SimpleZipFile;
import org.apel.show.attach.service.util.ZipUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjhy.inline.office.base.FileConvert;
import com.bjhy.inline.office.domain.Office;
import com.google.common.collect.Maps;

	
@Controller
@RequestMapping("nativeFileInfo")
public class NativeFileInfoController {
	
	@Value("${http_attachment_url}")
	private String httpAttachmentUrl;
	
	@Reference(timeout=30000)
	private FileInfoProviderService fileInfoProviderService;
	
	@RequestMapping(value = "/findFileInfo", method = RequestMethod.GET)
	public @ResponseBody PageBean findSelectedCriminal(QueryParams queryParams){
		JqGridUtil.getPageBean(queryParams);
		PageBean pageBean = JqGridUtil.getPageBean(queryParams);
		pageBean = fileInfoProviderService.findFileInfo(pageBean);
		return pageBean;
	}
	
	/**
	 * 更新文件信息
	 * @param newFileInfo
	 * @return
	 */
	@RequestMapping(value="updateFileInfoById",method = RequestMethod.POST)
	public @ResponseBody Message updateFileInfoById(FileInfo newFileInfo){
		FileInfo fileInfo = fileInfoProviderService.findFileById(newFileInfo.getId());
		BeanUtils.copyNotNullProperties(newFileInfo,fileInfo);
		fileInfoProviderService.update(fileInfo);
		return new Message(0,"更新成功");
	}
	
	//批量删除
	@RequestMapping(method = RequestMethod.DELETE)
	public @ResponseBody Message batchDelete(@RequestParam("ids[]") String[] ids){
		for (String id : ids) {
			FileInfo fileInfo = fileInfoProviderService.findFileById(id);
			fileInfoProviderService.deleteFile(fileInfo);
			fileInfoProviderService.deleteById(id);
		}
		return new Message(0,"文件删除成功");
	}
	
	@RequestMapping(value = "fileUpload",method = RequestMethod.POST)
	public String fileUpload(HttpServletRequest request,MultipartFile uploadFile){
		try {
			
			InputStream inputStream = uploadFile.getInputStream();
			
			byte[] bytes = uploadFile.getBytes();
			
			
			
			String userId = request.getParameter("userId");
			String businessId = request.getParameter("businessId");
			String fileNamePath = request.getParameter("fileName");
			fileNamePath = FileUtil.replaceSprit(fileNamePath);
			fileNamePath = fileNamePath.substring(fileNamePath.lastIndexOf("/")+1);
			
			String[] fileNameArray = fileNamePath.split("\\.");
			
			Map<String,String> params = new HashMap<String,String>();
			params.put("userId", userId);
			params.put("businessId", businessId);
			params.put("fileSuffix", fileNameArray[1]);
			//文件名称在 sendSingleFile 这个方法的第三个参数中传递
			HttpClientUtil.sendSingleFile(httpAttachmentUrl+"/httpFileInfo/fileSingleUpload", inputStream, fileNameArray[0], params, FileInfo.class);
		
			
		} catch (Exception e) {
			System.out.println("当前返回值有点问题");
		}
		
		request.setAttribute("data", "{'result':1}");
		return "common/upload_attach_result";
	}
	
	/**
	 * 更改zip的排序号
	 * @param businessId
	 */
	private void updateZipSort(String businessId){
		List<FileInfo> fileInfoList = fileInfoProviderService.findByBusinessId(businessId);
		for (FileInfo fileInfo : fileInfoList) {
			String fileName = fileInfo.getFileName();
			
			String[] fileNameArray = fileName.split("\\.");
			fileName = fileNameArray[0];
			Integer fileSort = null;
			try {
				fileSort = Integer.parseInt(fileName);
			} catch (NumberFormatException e) {
				fileSort = 0;
			}
			fileInfo.setFileSort(fileSort);
			fileInfoProviderService.update(fileInfo);
		}
	}
	
	private void sendStoreFile(File file,HashMap<String, String> param){
		String fileName = file.getName();
		String fileSuffix = "";
		String[] fileNameArray = fileName.split("\\.");
		fileName = fileNameArray[0];
		fileSuffix = fileNameArray[1];
		//设置参数
		Map<String,String> params = new HashMap<String,String>();
		params.putAll(param);
		params.put("fileSuffix", fileSuffix);
		
		try {
			FileInputStream is = new FileInputStream(file);
			HttpClientUtil.sendSingleFile(httpAttachmentUrl+"/httpFileInfo/fileSingleUpload", is, fileName, params, FileInfo.class);
			System.out.println();
		} catch (FileNotFoundException e) {
			System.out.println("当前返回值有点问题");
		}
		
		
		
	}
	
	/**
	 * 文件下载
	 * 注意:下载时必须重写hessian中 com.caucho.hessian.client.HessianProxy 类,修复流被关闭的情况
	 * 2.dubbo是用hessian作为协议是,提供的接口中的方法不能有重写的方法
	 * @param fileId
	 * @return
	 */
	@RequestMapping(value = "downloadFile",method = RequestMethod.GET)
	public ResponseEntity<byte[]> downloadFile(HttpServletRequest request,HttpServletResponse response,String fileId){
		try {
			String fileName = "";
			FileInfo fileInfo = fileInfoProviderService.findFileById(fileId); 
			
			fileName = fileInfo.getFileName()+"."+fileInfo.getFileSuffix();
			byte[] image = fileInfoProviderService.getFileBytes1(fileInfo.getRelativePath());
	        HttpHeaders responseHeaders = new HttpHeaders();
	        
	        responseHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	        responseHeaders.setContentLength(image.length);
	        responseHeaders.set("Content-Disposition", "attachment;filename=\""+URLEncoder.encode(fileName,"UTF-8")+"\"");
	        return new ResponseEntity<byte[]>(image,responseHeaders, HttpStatus.OK);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        return null;
	}
	
	@RequestMapping(value = "downloadZipFile",method = RequestMethod.GET)
	public void downloadFile(HttpServletRequest request,HttpServletResponse response,String fileIds,String businessId){
		List<String> fileIdList = new ArrayList<String>();
		
		if(businessId == null){
			return;
		}
		
		if(StringUtils.isEmpty(fileIds)){
			List<FileInfo> findByBusinessId = fileInfoProviderService.findByBusinessId(businessId);
			for (FileInfo fileInfo : findByBusinessId) {
				fileIdList.add(fileInfo.getId());
			}
		}else{
			String[] split = fileIds.split(",");
			for (String id : split) {
				fileIdList.add(id);
			}
		}
		
		SimpleZipFile simpleZipFile = getZipInputStream(fileIdList);
		downloadLargeFile(request, response, simpleZipFile.getFileName()+".zip", simpleZipFile.getFileSize(), simpleZipFile.getInputStream(),simpleZipFile.getFilePath());
	}
	
	/**
	 * 
	 * @param response
	 * @param fileIds
	 */
	private void downloadLargeFile(HttpServletRequest request,HttpServletResponse response,String fileName,long fileSize,InputStream is,String filePath){
		int BUFFER_SIZE = 4096;
		InputStream in = null;
		OutputStream out = null;

		try {
		     response.setContentType("application/x-download");
             response.addHeader("Content-Disposition","attachment;filename="+ new String(fileName.getBytes(),"utf-8"));
             response.addHeader("Content-Length", "" + (int)fileSize);   
             response.setContentType("application/octet-stream");   
             
			int readLength = 0;

			in = new BufferedInputStream(is, BUFFER_SIZE);
			out = new BufferedOutputStream(response.getOutputStream());

			byte[] buffer = new byte[BUFFER_SIZE];
			while ((readLength = in.read(buffer)) > 0) {
				byte[] bytes = new byte[readLength];
				System.arraycopy(buffer, 0, bytes, 0, readLength);
				out.write(bytes);
			}

			out.flush();

			response.addHeader("status", "1");

		} catch (Exception e) {
			e.printStackTrace();
			response.addHeader("status", "0");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
			
			//删除该临时zip文件
			try {
				UnZipStorePath.deleteFile(filePath);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private SimpleZipFile getZipInputStream(List<String> fileIds){
		
		SimpleZipFile simpleZipFile = new UnZipStorePath().new SimpleZipFile();
		String unZipDirectory = UnZipStorePath.getUnZipDirectory();
		UnZipStorePath.createUnZipDirectory(unZipDirectory);
		
		for (String fileId : fileIds) {
			FileInfo fileInfo = fileInfoProviderService.findFileById(fileId); 
			String relativePath = fileInfo.getRelativePath();
			Map<String,String> param = new HashMap<String,String>();
			param.put("relativePath", relativePath);
			
			HttpEntity syncReceiveSingleFile = HttpClientUtil.syncReceiveSingleFile("http://localhost:6696/returnFileStream", param); 
			try (InputStream content = syncReceiveSingleFile.getContent()){
				String tempFilePath = UnZipStorePath.replaceSprit(unZipDirectory+"/"+fileInfo.getFileName()+"."+fileInfo.getFileSuffix());
				
				FileUtils.copyInputStreamToFile(content, new File(tempFilePath));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		String zipFilePath = unZipDirectory+".zip";
		
		ZipUtil.zip(unZipDirectory,zipFilePath );
		
		
		FileInputStream fileInputStream = null;
		
		try {
			//删除该临时文件夹
			UnZipStorePath.deleteFile(unZipDirectory);
			
			File file = new File(zipFilePath);
			fileInputStream = new FileInputStream(file);
			simpleZipFile.setFileName(UnZipStorePath.getUUID());
			simpleZipFile.setFileSize(file.length());
			simpleZipFile.setInputStream(fileInputStream);
			simpleZipFile.setFilePath(zipFilePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return simpleZipFile;
	}
	
	/** 
     * 获得InputStream的byte数组 
     */  
	public byte[] getBytes(InputStream is) {
		byte[] buffer = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
		byte[] b = new byte[1000];
		try {
			int n = 0;
			while ((n = is.read(b)) != -1) {
				bos.write(b, 0, n);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		buffer = bos.toByteArray();
		return buffer;
	}
	
	/**
	 * 显示文件内容
	 * @param fileId 文件Id
	 * @return
	 */
	@RequestMapping("showFileContext")
	public @ResponseBody Office showFileContext(String fileId){
		
		if(StringUtils.isEmpty(fileId)){
			return null;
		}
		
		FileInfo fileInfo = fileInfoProviderService.findFileById(fileId); 
		byte[] fileByteArray = fileInfoProviderService.getFileBytes1(fileInfo.getRelativePath());
		
		FileConvert fileConvert = new FileConvert(new ByteArrayInputStream(fileByteArray),fileInfo.getFileName(),fileInfo.getFileSuffix(),"html");
		Office office = fileConvert.getOffice();
		return office;
	}
	
	/**
	 * 得到文件信息
	 * @param fileId 文件Id
	 * @return
	 */
	@RequestMapping("getFileInfo")
	public @ResponseBody FileInfo getFileInfo(String fileId){
		
		if(StringUtils.isEmpty(fileId)){
			return null;
		}
		
		FileInfo fileInfo = fileInfoProviderService.findFileById(fileId); 
		return fileInfo;
	}
	
	
	
	@RequestMapping("file_progress")
	public @ResponseBody Integer netCheck(HttpSession session){
//		Object progress = session.getAttribute(FileUploadProgressListener.PROGRESS_SESSION);
//		if(!Objects.isNull(progress)){
//			return (Integer)progress;
//		}
		return 0;
	}
	    
	/**
	 * 弹出office新页面
	 * @param request
	 * @return
	 */
	@RequestMapping("newOfficePage")
	public String newOfficePage(HttpServletRequest request){
		return "common/show_office_page";
	}
	

}
