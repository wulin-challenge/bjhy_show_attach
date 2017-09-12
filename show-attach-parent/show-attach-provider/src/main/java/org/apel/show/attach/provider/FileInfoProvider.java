package org.apel.show.attach.provider;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apel.gaia.commons.pager.PageBean;
import org.apel.show.attach.core.base.FileStoreServer;
import org.apel.show.attach.core.domain.FileInfoEntity;
import org.apel.show.attach.core.service.FileInfoService;
import org.apel.show.attach.core.service.impl.FileInfoServiceImpl;
import org.apel.show.attach.service.domain.FileInfo;
import org.apel.show.attach.service.service.FileInfoProviderService;
import org.jboss.logging.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;

@Service
@Component
public class FileInfoProvider implements FileInfoProviderService{
	
	Logger logger = Logger.getLogger(FileInfoServiceImpl.class);
	
	@Autowired
	private FileInfoService fileInfoService;
	
	@Autowired
	private FileStoreServer fileStoreServer;

	@Override
	public void store(FileInfo fileInfo) {
		FileInfoEntity fileInfoEntity = new FileInfoEntity();
		BeanUtils.copyProperties(fileInfo, fileInfoEntity);
		fileInfoService.store(fileInfoEntity);
	}

	@Override
	public void update(FileInfo fileInfo) {
		FileInfoEntity fileInfoEntity = new FileInfoEntity();
		BeanUtils.copyProperties(fileInfo, fileInfoEntity);
		fileInfoService.update(fileInfoEntity);
	}

	@Override
	public FileInfo findOne(String id) {
		FileInfoEntity fileInfoEntity = fileInfoService.findOne(id);
		FileInfo fileInfo = new FileInfo();
		BeanUtils.copyProperties(fileInfoEntity,fileInfo);
		return fileInfo;
	}

	@Override
	public List<FileInfo> findByBusinessId(String businessId) {
		List<FileInfo> fileInfoList = new ArrayList<FileInfo>();
		List<FileInfoEntity> fileInfoEntityList = fileInfoService.findByBusinessId(businessId);
		for (FileInfoEntity fileInfoEntity : fileInfoEntityList) {
			FileInfo fileInfo = new FileInfo();
			BeanUtils.copyProperties(fileInfoEntity,fileInfo);
			fileInfoList.add(fileInfo);
		}
		return fileInfoList;
	}

	@Override
	public void deleteById(String id) {
		
		try{
			fileInfoService.deleteById(id);
		}catch(Exception e){
			logger.error(e.getMessage());
		}
	}

	@Override
	public void deleteByBusinessId(String businessId) {
		try{
			fileInfoService.deleteByBusinessId(businessId);
		}catch(Exception e){
			logger.error(e.getMessage());
		}
		
	}

	@Override
	public PageBean findFileInfo(PageBean pageBean) {
		fileInfoService.findFileInfo(pageBean);
		List<Object> list = pageBean.getItems();
		
		List<FileInfo> fileInfoList = new ArrayList<FileInfo>();
		for (Object object : list) {
			
			FileInfo fileInfo = new FileInfo();
			
			BeanUtils.copyProperties(object, fileInfo);
			fileInfoList.add(fileInfo);
			
		}
		pageBean.setItems(fileInfoList);
		
		return pageBean;
	}

	@Override
	public FileInfo storeFile1(String businessId,String fileName, String fileSuffix,long fileSize,byte[] filebyteArray) {
		FileInfoEntity fileInfoEntity =  fileStoreServer.storeFile(businessId, filebyteArray, fileSize,fileName, fileSuffix);
		FileInfo fileInfo = new FileInfo();
		BeanUtils.copyProperties(fileInfoEntity,fileInfo);
		return fileInfo;
	}

	@Override
	public FileInfo storeFile2(String businessId, String userId,String fileName, String fileSuffix,long fileSize,byte[] filebyteArray) {
		FileInfoEntity fileInfoEntity =  fileStoreServer.storeFile(businessId, userId, filebyteArray, fileSize,fileName, fileSuffix);
		FileInfo fileInfo = new FileInfo();
		BeanUtils.copyProperties(fileInfoEntity,fileInfo);
		return fileInfo;
	}

	@Override
	public FileInfo updateFile(FileInfo fileInfo, long fileSize,InputStream is) {
		FileInfoEntity fileInfoEntity =  new FileInfoEntity();
		BeanUtils.copyProperties(fileInfo, fileInfoEntity);
		fileInfoEntity = fileStoreServer.updateFile(fileInfoEntity, fileSize,is);
		FileInfo fileInfo2 = new FileInfo();
		BeanUtils.copyProperties(fileInfoEntity,fileInfo2);
		return fileInfo2;
	}

	@Override
	public FileInfo deleteFile(FileInfo fileInfo) {
		FileInfoEntity fileInfoEntity =  new FileInfoEntity();
		BeanUtils.copyProperties(fileInfo, fileInfoEntity);
		fileInfoEntity = fileStoreServer.deleteFile(fileInfoEntity);
		FileInfo fileInfo2 = new FileInfo();
		BeanUtils.copyProperties(fileInfoEntity,fileInfo2);
		return fileInfo2;
	}

	@Override
	public List<FileInfo> findFileByBusinessId(String businessId) {
		List<FileInfo> fileInfoList = new ArrayList<FileInfo>();
		List<FileInfoEntity> fileInfoEntityList = fileStoreServer.findFileByBusinessId(businessId);
		for (FileInfoEntity fileInfoEntity : fileInfoEntityList) {
			FileInfo fileInfo = new FileInfo();
			BeanUtils.copyProperties(fileInfoEntity,fileInfo);
			fileInfoList.add(fileInfo);
		}
		return fileInfoList;
		
	}

	@Override
	public FileInfo findFileById(String id) {
		FileInfoEntity fileInfoEntity =  fileStoreServer.findFileById(id);
		FileInfo fileInfo = new FileInfo();
		BeanUtils.copyProperties(fileInfoEntity,fileInfo);
		return fileInfo;
	}

	@Override
	public byte[] getFileBytes2(FileInfo fileInfo) {
		FileInfoEntity fileInfoEntity = new FileInfoEntity();
		BeanUtils.copyProperties(fileInfo,fileInfoEntity);
		return fileStoreServer.getFileBytes(fileInfoEntity);
	}

//	@Override
//	public InputStream getFileInputStream1(FileInfo fileInfo) {
//		FileInfoEntity fileInfoEntity = new FileInfoEntity();
//		BeanUtils.copyProperties(fileInfo,fileInfoEntity);
//		return fileStoreServer.getFileInputStream(fileInfoEntity);
//	}

	@Override
	public byte[] getFileBytes1(String relativePath) {
		return fileStoreServer.getFileBytes(relativePath);
	}

//	@Override
//	public InputStream getFileInputStream2(String relativePath) {
//		return fileStoreServer.getFileInputStream(relativePath);
//	}

	@Override
	public String getRootDirectory() {
		return fileStoreServer.getRootDirectory();
	}

	@Override
	public boolean isFile(String relativePath) {
		return fileStoreServer.isFile(relativePath);
	}

	@Override
	public void isItEffective(FileInfo fileInfo) {
		isItEffective(fileInfo);
	}

}
