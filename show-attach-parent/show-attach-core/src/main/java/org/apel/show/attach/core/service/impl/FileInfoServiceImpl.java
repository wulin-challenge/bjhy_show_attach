package org.apel.show.attach.core.service.impl;


import java.util.List;

import org.apel.gaia.commons.pager.PageBean;
import org.apel.show.attach.core.dao.FileInfoRepository;
import org.apel.show.attach.core.domain.FileInfoEntity;
import org.apel.show.attach.core.service.FileInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FileInfoServiceImpl implements FileInfoService{
	
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
	public void deleteById(String id) {
		 fileInfoRepository.delete(id);
	}

	@Override
	public void deleteByBusinessId(String businessId) {
		List<FileInfoEntity> fileInfoList = findByBusinessId(businessId);
		for (FileInfoEntity fileInfo : fileInfoList) {
			fileInfoRepository.delete(fileInfo);
		}
	}

	@Override
	public void findFileInfo(PageBean pageBean) {
		String hql =  "select o.id,o.businessId,o.userId,o.fileName,o.fileSuffix,o.fileSize,o.relativePath,o.uploadTime from FileInfoEntity o where 1=1 ";
		fileInfoRepository.doPager(pageBean, hql);
		System.out.println();
	}
}
