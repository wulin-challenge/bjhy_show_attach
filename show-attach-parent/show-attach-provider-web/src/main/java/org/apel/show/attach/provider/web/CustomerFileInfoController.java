package org.apel.show.attach.provider.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apel.gaia.commons.i18n.Message;
import org.apel.gaia.commons.jqgrid.QueryParams;
import org.apel.gaia.commons.pager.PageBean;
import org.apel.gaia.container.boot.customize.multipart.FileUploadProgressListener;
import org.apel.gaia.util.BeanUtils;
import org.apel.gaia.util.jqgrid.JqGridUtil;
import org.apel.show.attach.provider.util.HttpClientUtil;
import org.apel.show.attach.service.domain.FileInfo;
import org.apel.show.attach.service.service.FileInfoProviderService;
import org.apel.show.attach.service.util.UnZipStorePath;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjhy.inline.office.base.FileConvert;
import com.bjhy.inline.office.domain.Office;
import com.google.common.collect.Maps;

	
@Controller
@RequestMapping("customerFileInfo")
@CrossOrigin
public class CustomerFileInfoController {
	
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
		System.out.println(); 
		for (String id : ids) {
			FileInfo fileInfo = fileInfoProviderService.findFileById(id);
			fileInfoProviderService.deleteFile(fileInfo);
			fileInfoProviderService.deleteById(id);
		}
		return new Message(1,"文件删除成功");
//		return MessageUtil.message("file.delete.success");
	}
	
	//文件上传(这是采用流传输方式实现上传必须屏蔽   spring.http.multipart.enabled=false)
	@RequestMapping(value = "fileUpload",method = RequestMethod.POST)
	public String fileUpload(HttpServletRequest request){
		try {
			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			if (!isMultipart) {
				request.setAttribute("data", "{result:0}");
				return "common/upload_result";
			}
			
			ServletFileUpload upload = new ServletFileUpload();

			FileItemIterator iter = upload.getItemIterator(request);
			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				InputStream is = item.openStream();
				if (!item.isFormField()) {
					
					String fileName = item.getName();
					String fileSuffix = "";
					String[] fileNameArray = fileName.split("\\.");
					fileName = fileNameArray[0];
					fileSuffix = fileNameArray[1];
					String userId = request.getParameter("userId");
					String businessId = request.getParameter("businessId");
					
					//设置参数
					HashMap<String, String> param = Maps.newHashMap();
					param.put("fileName", fileName);
					param.put("fileSuffix", fileSuffix);
					param.put("userId", userId);
					param.put("businessId", businessId);
					
					if("zip".equalsIgnoreCase(fileSuffix)){
						storeZipFile(is, fileName, businessId, userId);
					}else{
						HttpClientUtil.syncSendSingleFile("http://localhost:6696/fileStore2", param, fileName, is, FileInfo.class); 
					}
				}
			}
		} catch (Exception e) {
			System.out.println("当前返回值有点问题");
		}
		
		request.setAttribute("data", "{result:1}");
		return "common/upload_result";
	}
	
	private void storeZipFile(InputStream is,String fileName,String businessId,String userId){
		try {
			Map<String, Object> writeDisc = UnZipStorePath.writeDisc(is, fileName);
			String unZipDirectory = (String) writeDisc.get("unZipDirectory");
			File unZipAfterDirectoryFile = (File) writeDisc.get("unZipAfterDirectoryFile");
			
			UnZipStorePath.readDirectory(unZipAfterDirectoryFile, new UnZipStorePath().new UnZipFileCallBack(){
	
				@Override
				public void fileCallBack(File unZipAfterDirectoryFile) {
					HashMap<String, String> param = Maps.newHashMap();
					param.put("fileName", fileName);
					param.put("userId", userId);
					param.put("businessId", businessId);
					
					sendStoreFile(unZipAfterDirectoryFile, param);
				}
			});
		
			UnZipStorePath.deleteFile(unZipDirectory);
			updateZipSort(businessId);//更改zip的排序号
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	
	private static void sendStoreFile(File file,HashMap<String, String> param){
		String fileName = file.getName();
		String fileSuffix = "";
		String[] fileNameArray = fileName.split("\\.");
		fileName = fileNameArray[0];
		fileSuffix = fileNameArray[1];
		//设置参数
		param.put("fileName", fileName);
		param.put("fileSuffix", fileSuffix);
		
		try {
			FileInputStream is = new FileInputStream(file);
			HttpClientUtil.syncSendSingleFile("http://localhost:6696/fileStore2", param, fileName, is, FileInfo.class); 
			System.out.println();
		} catch (FileNotFoundException e) {
			System.out.println("当前返回值有点问题");
		}
		
		
		
	}
	
//	//文件上传(这是采用流传输方式实现上传必须屏蔽   spring.http.multipart.enabled=false)
//	@RequestMapping(value = "fileUpload",method = RequestMethod.POST)
//	public String fileUpload(HttpServletRequest request){
//		try {
////			String id = request.getParameter("id");//获取app的数据库主键
//			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
//			if (!isMultipart) {
//				return "error";
//			}
//			ServletFileUpload upload = new ServletFileUpload();
//
//			FileItemIterator iter = upload.getItemIterator(request);
//			while (iter.hasNext()) {
//				FileItemStream item = iter.next();
//				InputStream stream = item.openStream();
//				if (!item.isFormField()) {
//					String fileName = item.getName();
//					HashMap<String, String> param = Maps.newHashMap();
//					param.put("id", "111");
//					param.put("fileName", fileName);
//					HttpClientUtil.syncSendSingleFile("http://localhost:6696/receiveFile", param, fileName, stream, String.class); 
//					
//					System.out.println(fileName);
////					applicationService.uploadJar(stream, fileName, id);
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
////			Throwables.throwIfUnchecked(e);
//		}
//		
//		
//		return null;
//	}
	
	
//	//文件上传这是传统采用 spring MultipartFile 的上传做法
//		@RequestMapping(value = "fileUpload",method = RequestMethod.POST)
//		public String fileUpload(MultipartFile uploadFile,HttpServletRequest request){
//			String fileName = "";
//			String fileSuffix = "";
//			long fileSize = 0l;
//			InputStream is = null; 
//			
//			try {
//				is = uploadFile.getInputStream();
//				fileSize = uploadFile.getSize();
//				String originalFileName = uploadFile.getOriginalFilename();
//				String[] fileNameArray = originalFileName.split("\\.");
//				fileName = fileNameArray[0];
//				fileSuffix = fileNameArray[1];
//				
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			
//			String userId = request.getParameter("userId");
//			String businessId = request.getParameter("businessId");
//			
//			byte[] fileByteArray = getBytes(is);
//			
//			FileInfo fileInfo = fileInfoProviderService.storeFile2(businessId,userId, fileName, fileSuffix,fileSize, fileByteArray);
//			System.out.println(fileInfo);
//			
//			request.setAttribute("data", "{result:1}");
//			return "common/upload_result";
//		}
		
	
	/**
	 * 文件下载
	 * 注意:下载时必须重写hessian中 com.caucho.hessian.client.HessianProxy 类,修复流被关闭的情况
	 * 2.dubbo是用hessian作为协议是,提供的接口中的方法不能有重写的方法
	 * @param fileId
	 * @return
	 */
	@RequestMapping(value = "downloadFile",method = RequestMethod.GET)
	public ResponseEntity<byte[]> downloadFile(String fileId){
		
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
	
	@RequestMapping("file_progress")
	public @ResponseBody Integer netCheck(HttpSession session){
		Object progress = session.getAttribute(FileUploadProgressListener.PROGRESS_SESSION);
		if(!Objects.isNull(progress)){
			return (Integer)progress;
		}
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
