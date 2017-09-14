package org.apel.show.attach.controller;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apel.show.attach.service.domain.FileInfo;
import org.apel.show.attach.service.service.FileInfoProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileInfoController {
	
	@Autowired
	private FileInfoProviderService fileInfoProviderService;
	
	/**
	 * 采用http调用的方式传输文件且是以流的方式
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/fileStore2",method = RequestMethod.POST)
	public FileInfo fileStore2(HttpServletRequest request){
		FileInfo fileInfo= null; 
		try {
			String userId = request.getParameter("userId");
			String businessId = request.getParameter("businessId");
			String fileSuffix = request.getParameter("fileSuffix");
			
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
					try (InputStream is = item.openStream();){
						fileInfo = fileInfoProviderService.storeFile2(businessId, userId, fileName, fileSuffix, is);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return fileInfo;
	}
	
	@RequestMapping(value = "/receiveFile",method = RequestMethod.POST)
	public String receiveFile(HttpServletRequest request){
		System.out.println();
		try {
			String id = request.getParameter("id");//获取app的数据库主键
			String fileName1 = request.getParameter("fileName");//获取app的数据库主键
			System.out.println(id);
			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			if (!isMultipart) {
				return "error";
			}
			ServletFileUpload upload = new ServletFileUpload();

			FileItemIterator iter = upload.getItemIterator(request);
			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				if (!item.isFormField()) {
					String fileName = item.getName();
					long start = System.currentTimeMillis();
					try (InputStream is = item.openStream();
							/* buffer增加IO读写性能,传统刷盘可用
							BufferedInputStream bis = new BufferedInputStream(is);
							BufferedOutputStream bos = new BufferedOutputStream(fos)
							*/
							FileOutputStream fos = new FileOutputStream("d:/temp/" + fileName);
							ReadableByteChannel inChannel = Channels.newChannel(is);
							FileChannel outChannel = fos.getChannel()){
						//IOUtils.copy(bis, bos); //传统IO拷贝
						
						//DMA内存操作
						outChannel.transferFrom(inChannel, 0, Long.MAX_VALUE);
						
					}
					System.out.println((Double.valueOf(String.valueOf(System.currentTimeMillis() - start))) / 1000 + "s");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
//			Throwables.throwIfUnchecked(e);
		}
		
		
		return null;
	}

}
