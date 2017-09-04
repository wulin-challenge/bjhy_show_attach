package org.apel.show.attach.core.base;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apel.show.attach.core.domain.FileInfoEntity;
import org.apel.show.attach.core.service.FileInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 文件存储服务
 * @author wubo
 *
 */
@Component
public class FileStoreServer {
	
	/**
	 * 存储文件的根目录
	 */
	@Value("${root_directory}")
	private String rootDirectory;
	
	@Autowired
	private FileInfoService fileInfoService;
	
	/**
	 * 存储文件信息到数据库,文件到磁盘
	 * @param businessId 业务Id
	 * @param is 文件流
	 * @param fileName 文件名
	 * @param fileSuffix 文件后缀
	 * @return 返回文件的信息
	 */
	public FileInfoEntity storeFile(String businessId,byte[] filebyteArray,long fileSize,String fileName,String fileSuffix){
		InputStream is = FileStorePath.byteToInputStream(filebyteArray);
		return storeFile(businessId, is,fileSize,fileName, fileSuffix);
	}
	
	/**
	 * 存储文件信息到数据库,文件到磁盘
	 * @param businessId 业务Id
	 * @param is 文件流
	 * @param fileName 文件名
	 * @param fileSuffix 文件后缀
	 * @return 返回文件的信息
	 */
	private FileInfoEntity storeFile(String businessId,InputStream is,long fileSize,String fileName,String fileSuffix){
		return storeFile(businessId, null, is,fileSize,fileName, fileSuffix);
	}
	
	/**
	 * 存储文件信息到数据库,文件到磁盘
	 * @param businessId 业务Id
	 * @param userId 用户Id
	 * @param is 文件流
	 * @param fileName 文件名
	 * @param fileSuffix 文件后缀
	 * @return 返回文件的信息
	 */
	public FileInfoEntity storeFile(String businessId,String userId,byte[] filebyteArray,long fileSize,String fileName,String fileSuffix){
		InputStream is = FileStorePath.byteToInputStream(filebyteArray);
		return storeFile(businessId, userId, is, fileSize, fileName, fileSuffix);
		
	}
	
	/**
	 * 存储文件信息到数据库,文件到磁盘
	 * @param businessId 业务Id
	 * @param userId 用户Id
	 * @param is 文件流
	 * @param fileName 文件名
	 * @param fileSuffix 文件后缀
	 * @return 返回文件的信息
	 */
	private FileInfoEntity storeFile(String businessId,String userId,InputStream is,long fileSize,String fileName,String fileSuffix){
		//存储文件到磁盘,并返回该文件的存储的路径信息
		FileStorePath fileStorePath = storeFileToDisk(is,fileName, fileSuffix);
		
		FileInfoEntity fileInfo = new FileInfoEntity();
		fileInfo.setId(fileStorePath.getUuid());
		fileInfo.setBusinessId(businessId);
		fileInfo.setFileName(fileName);
		fileInfo.setFileSuffix(fileSuffix);
		fileInfo.setFileSize(FileStorePath.formetFileSize(fileSize));
		fileInfo.setRelativePath(fileStorePath.getRelativePath());
		fileInfo.setUserId(userId);
		fileInfo.setUploadTime(new Date());
		fileInfo.setFileStatus(true);
		fileInfoService.store(fileInfo);
		return fileInfo;
	}
	
	/**
	 * 更新文件及文件在数据库的信息
	 * @param fileInfo 要更新的文件信息
	 * @param is 文件输入流
	 * @return 返回更新文件后的文件信息
	 */
	public FileInfoEntity updateFile(FileInfoEntity fileInfo,long fileSize,InputStream is){
		try {
			String reallyPath = FileStorePath.getRootDirectory(getRootDirectory())+fileInfo.getRelativePath();
			FileStorePath.deleteFile(reallyPath);
			
			//存储文件到磁盘,并返回该文件的存储的路径信息
			FileStorePath fileStorePath = storeFileToDisk(is,fileInfo.getFileName(), fileInfo.getFileSuffix());
			fileInfo.setRelativePath(fileStorePath.getRelativePath());
			fileInfo.setUploadTime(new Date());
			fileInfo.setFileStatus(true);
			fileInfo.setFileSize(FileStorePath.formetFileSize(fileSize));
			
			fileInfoService.update(fileInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileInfo;
	}
	
	/**
	 * 删除磁盘中的文件以及数据库中的文件信息
	 * @param fileInfo 将被删除的文件信息
	 * @return 以被删除的文件信息
	 */
	public FileInfoEntity deleteFile(FileInfoEntity fileInfo){
		try {
			String reallyPath = FileStorePath.getRootDirectory(getRootDirectory())+fileInfo.getRelativePath();
			FileStorePath.deleteFile(reallyPath);
			
			fileInfoService.deleteById(fileInfo.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileInfo;
	}
	
	/**
	 * 存储文件到磁盘,并返回该文件的存储的路径信息
	 * @param is
	 * @param rootDirectory
	 * @param fileName
	 * @param fileSuffix
	 * @return
	 */
	private FileStorePath storeFileToDisk(InputStream is,String fileName,String fileSuffix){
		FileStorePath fileStorePath = new FileStorePath(getRootDirectory(),fileName,fileSuffix);
		//创建目录
		fileStorePath.createDirectory(fileStorePath.getReallyDirecotory());
		
		try {
			FileUtils.copyInputStreamToFile(is, new File(fileStorePath.getReallyPath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		return fileStorePath;
	}
	
	/**
	 * 通过业务Id查找文件信息
	 * @param BusinessId
	 */
	public List<FileInfoEntity> findFileByBusinessId(String businessId){
		return fileInfoService.findByBusinessId(businessId);
	}
	
	/**
	 * 通过Id查找文件信息
	 * @param BusinessId
	 */
	public FileInfoEntity findFileById(String id){
		return fileInfoService.findOne(id);
	}
	
	/**
	 * 得到文件的字节
	 * @param fileInfo 文件信息
	 * @return 文件字节
	 */
	public byte[] getFileBytes(FileInfoEntity fileInfo){
		if(fileInfo == null){
			return null;
		}
		String reallyPath = FileStorePath.getRootDirectory(getRootDirectory())+fileInfo.getRelativePath();
		return FileStorePath.getBytes(FileStorePath.getInputStreamByFilePath(reallyPath));
	}
	
	/**
	 * 得到文件的输入流
	 * @param fileInfo 文件信息对象
	 * @return 文件的输入流
	 */
	private InputStream getFileInputStream(FileInfoEntity fileInfo){
		if(fileInfo == null){
			return null;
		}
		String reallyPath = FileStorePath.getRootDirectory(getRootDirectory())+fileInfo.getRelativePath();
		return FileStorePath.getInputStreamByFilePath(reallyPath);
	}
	
	/**
	 * 得到文件的字节
	 * @param relativePath 存储文件相对路径
	 * @return 文件字节
	 */
	public byte[] getFileBytes(String relativePath){
		if(StringUtils.isEmpty(relativePath)){
			return null;
		}
		relativePath = FileStorePath.replaceSprit(relativePath);
		String reallyPath = FileStorePath.getRootDirectory(getRootDirectory())+relativePath;
		return FileStorePath.getBytes(FileStorePath.getInputStreamByFilePath(reallyPath));
	}
	
	/**
	 * 得到文件的输入流
	 * @param relativePath 存储人家相对路径
	 * @return 文件的输入流
	 */
	private InputStream getFileInputStream(String relativePath){
		if(StringUtils.isEmpty(relativePath)){
			return null;
		}
		relativePath = FileStorePath.replaceSprit(relativePath);
		String reallyPath = FileStorePath.getRootDirectory(getRootDirectory())+relativePath;
		return FileStorePath.getInputStreamByFilePath(reallyPath);
	}
	
	/**
	 * 得到文件的根目录
	 * @return
	 */
	public String getRootDirectory(){
		return rootDirectory;
	}
	
	/**
	 * 判断是否是文件,是文件返回true,否则返回false
	 * @param relativePath 文件的相对路径
	 * @return 是文件返回true,否则返回false
	 */
	public boolean isFile(String relativePath){
		if(StringUtils.isEmpty(relativePath)){
			return false;
		}
		relativePath = FileStorePath.replaceSprit(relativePath);
		String reallyPath = FileStorePath.getRootDirectory(getRootDirectory())+relativePath;
		File file = new File(reallyPath);
		return file.isFile();
	}
	
	/**
	 * 检测文件是否有效,并更新文件状态进数据库
	 * @param fileInfo
	 */
	public void isItEffective(FileInfoEntity fileInfo){
		boolean isFile = isFile(fileInfo.getRelativePath());
		if(isFile != fileInfo.getFileStatus()){
			fileInfo.setFileStatus(isFile);
			fileInfoService.update(fileInfo);
		}
	}
	
}
