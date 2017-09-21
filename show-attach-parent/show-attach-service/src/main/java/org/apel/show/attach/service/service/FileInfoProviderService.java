package org.apel.show.attach.service.service;

import java.io.InputStream;
import java.util.List;

import org.apel.gaia.commons.pager.PageBean;
import org.apel.show.attach.service.domain.FileInfo;

public interface FileInfoProviderService {
	
	/**
	 * 保存
	 * @param fileInfo
	 */
	public void store(FileInfo fileInfo);
	
	/**
	 * 更新
	 * @param fileInfo
	 */
	public void update(FileInfo fileInfo);
	
	/**
	 * 通过Id查找
	 * @param id
	 * @return
	 */
	public FileInfo findOne(String id);
	
	/**
	 * 通过业务Id进行查找
	 * @param businessId
	 * @return
	 */
	public List<FileInfo> findByBusinessId(String businessId);
	
	/**
	 * 通过Id删除
	 * @param id
	 */
	public void deleteById(String id);
	
	/**
	 * 通过业务Id进行删除
	 * @param businessId
	 */
	public void deleteByBusinessId(String businessId);
	
	/**
	 * 查找文件信息,这是用于grid的显示的
	 * @param pageBean
	 * @return
	 */
	public PageBean findFileInfo(PageBean pageBean);	
	
	/**
	 * 存储文件信息到数据库,文件到磁盘
	 * @param businessId 业务Id
	 * @param is 文件流
	 * @param fileName 文件名
	 * @param fileSuffix 文件后缀
	 * @return 返回文件的信息
	 */
	public FileInfo storeFile1(String businessId,String fileName,String fileSuffix,long fileSize,byte[] filebyteArray);
	
	/**
	 * 存储文件信息到数据库,文件到磁盘
	 * @param businessId 业务Id
	 * @param userId 用户Id
	 * @param is 文件流
	 * @param fileName 文件名
	 * @param fileSuffix 文件后缀
	 * @return 返回文件的信息
	 */
	public FileInfo storeFile2(String businessId,String userId,String fileName,String fileSuffix,long fileSize,byte[] filebyteArray);
	
	/**
	 * 存储文件信息到数据库,文件到磁盘
	 * @param businessId 业务Id
	 * @param userId 用户Id
	 * @param is 文件流
	 * @param fileName 文件名
	 * @param fileSuffix 文件后缀
	 * @return 返回文件的信息
	 */
	public FileInfo storeFile2(String businessId, String userId, String fileName,String fileSuffix, InputStream is);
	
	/**
	 * 更新文件及文件在数据库的信息
	 * @param fileInfo 要更新的文件信息
	 * @param is 文件输入流
	 * @return 返回更新文件后的文件信息
	 */
	public FileInfo updateFile(FileInfo fileInfo,long fileSize,InputStream is);
	
	/**
	 * 删除磁盘中的文件以及数据库中的文件信息
	 * @param fileInfo 将被删除的文件信息
	 * @return 以被删除的文件信息
	 */
	public FileInfo deleteFile(FileInfo fileInfo);
	
	/**
	 * 通过业务Id查找文件信息
	 * @param BusinessId
	 */
	public List<FileInfo> findFileByBusinessId(String businessId);
	
	/**
	 * 通过Id查找文件信息
	 * @param BusinessId
	 */
	public FileInfo findFileById(String id);
	
	/**
	 * 得到文件的字节
	 * @param fileInfo 文件信息
	 * @return 文件字节
	 */
	public byte[] getFileBytes2(FileInfo fileInfo);
	
//	/**
//	 * 得到文件的输入流
//	 * @param fileInfo 文件信息对象
//	 * @return 文件的输入流
//	 */
//	public InputStream getFileInputStream1(FileInfo fileInfo);
	
	/**
	 * 得到文件的字节
	 * @param relativePath 存储文件相对路径
	 * @return 文件字节
	 */
	public byte[] getFileBytes1(String relativePath);
	
	/**
	 * 得到文件的输入流
	 * @param relativePath 存储人家相对路径
	 * @return 文件的输入流
	 */
	public InputStream getFileInputStream2(String relativePath);
	
	/**
	 * 得到文件的根目录
	 * @return
	 */
	public String getRootDirectory();
	
	/**
	 * 判断是否是文件,是文件返回true,否则返回false
	 * @param relativePath 文件的相对路径
	 * @return 是文件返回true,否则返回false
	 */
	public boolean isFile(String relativePath);
	
	/**
	 * 检测文件是否有效,并更新文件状态进数据库
	 * @param fileInfo
	 */
	public void isItEffective(FileInfo fileInfo);
	
	/**
	 * 通过业务Id查找最大排序值
	 * @param businessId
	 * @return
	 */
	public Integer findMaxByBusinessId(String businessId);


}
