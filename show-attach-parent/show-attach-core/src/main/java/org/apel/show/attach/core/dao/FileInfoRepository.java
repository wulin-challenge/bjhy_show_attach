package org.apel.show.attach.core.dao;

import java.util.List;

import org.apel.gaia.persist.dao.CommonRepository;
import org.apel.show.attach.core.domain.FileInfoEntity;

public interface FileInfoRepository extends CommonRepository<FileInfoEntity, String>{

	/**
	 * 通过业务Id进行查找文件信息
	 * @param businessId
	 * @return
	 */
	public List<FileInfoEntity> findByBusinessId(String businessId);
}
