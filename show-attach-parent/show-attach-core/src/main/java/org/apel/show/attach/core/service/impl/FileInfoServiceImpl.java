package org.apel.show.attach.core.service.impl;


import java.util.List;

import org.apel.gaia.commons.pager.PageBean;
import org.apel.show.attach.core.dao.FileInfoRepository;
import org.apel.show.attach.core.domain.FileInfoEntity;
import org.apel.show.attach.core.service.FileInfoService;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FileInfoServiceImpl implements FileInfoService{
	
	Logger logger = Logger.getLogger(FileInfoServiceImpl.class);
	
	@Autowired
	private FileInfoRepository fileInfoRepository;

	@Override
	public void store(FileInfoEntity fileInfo) {
		fileInfoRepository.store(fileInfo);
	}

	@Override
	public void update(FileInfoEntity fileInfo) {
		fileInfoRepository.update(fileInfo);
	}

	@Override
	public FileInfoEntity findOne(String id) {
		return fileInfoRepository.findOne(id);
	}

	@Override
	public List<FileInfoEntity> findByBusinessId(String businessId) {
		return fileInfoRepository.findByBusinessId(businessId);
	}
	
	@Override
	public List<FileInfoEntity> findByIds(List<String> ids) {
		return fileInfoRepository.findByIds(ids);
	}

	@Override
	public void deleteById(String id) {
		FileInfoEntity findOne = fileInfoRepository.findOne(id);
		if(findOne != null){
			fileInfoRepository.delete(id);
		}else{
			logger.info("当前数据不存在,id="+id);
		}
		
	}

	@Override
	public void deleteByBusinessId(String businessId) {
		List<FileInfoEntity> fileInfoList = findByBusinessId(businessId);
		for (FileInfoEntity fileInfo : fileInfoList) {
			if(fileInfo != null){
				fileInfoRepository.delete(fileInfo);
			}else{
				logger.info("当前数据不存在,businessId="+businessId);
			}
			
		}
	}

	@Override
	public void findFileInfo(PageBean pageBean) {
		String hql =  "select o.id,o.businessId,o.userId,o.fileName,o.fileSort,o.fileSuffix,o.fileSize,o.relativePath,o.uploadTime from FileInfoEntity o where 1=1 order by o.fileSort desc";
		fileInfoRepository.doPager(pageBean, hql);
		System.out.println();
	}

	@Override
	public Integer findMaxByBusinessId(String businessId) {
		synchronized (this) {
			Integer findMaxByBusinessId = fileInfoRepository.findMaxByBusinessId(businessId);
			if(findMaxByBusinessId == null || findMaxByBusinessId ==0){
				findMaxByBusinessId = 1;
			}else{
				findMaxByBusinessId +=1;
			}
			return findMaxByBusinessId;
		}
	}

}
