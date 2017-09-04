package org.apel.show.attach.service.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apel.gaia.commons.i18n.Message;
import org.apel.gaia.commons.jqgrid.QueryParams;
import org.apel.gaia.commons.pager.PageBean;
import org.apel.gaia.util.jqgrid.JqGridUtil;
import org.apel.show.attach.service.domain.FileInfo;
import org.apel.show.attach.service.service.FileInfoProviderService;
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

	
//@Controller
//@RequestMapping("customerFileInfo")

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
	
	//批量删除
	@RequestMapping(method = RequestMethod.DELETE)
	public @ResponseBody Message batchDelete(@RequestParam("ids[]") String[] ids){
		System.out.println(); 
		for (String id : ids) {
			fileInfoProviderService.deleteById(id);
		}
		return new Message(1,"文件删除成功");
//		return MessageUtil.message("file.delete.success");
	}
	
	//文件上传
	@RequestMapping(value = "fileUpload",method = RequestMethod.POST)
	public String fileUpload(MultipartFile uploadFile,HttpServletRequest request){
		String fileName = "";
		String fileSuffix = "";
		long fileSize = 0l;
		InputStream is = null; 
		
		try {
			is = uploadFile.getInputStream();
			fileSize = uploadFile.getSize();
			String originalFileName = uploadFile.getOriginalFilename();
			String[] fileNameArray = originalFileName.split("\\.");
			fileName = fileNameArray[0];
			fileSuffix = fileNameArray[1];
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String userId = request.getParameter("userId");
		String businessId = request.getParameter("businessId");
		
		byte[] fileByteArray = getBytes(is);
		
		FileInfo fileInfo = fileInfoProviderService.storeFile2(businessId,userId, fileName, fileSuffix,fileSize, fileByteArray);
		System.out.println(fileInfo);
		
		request.setAttribute("data", "{result:1}");
		return "common/upload_result";
	}
	
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
			byte[] image =  fileInfoProviderService.getFileBytes1(fileInfo.getRelativePath());
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
