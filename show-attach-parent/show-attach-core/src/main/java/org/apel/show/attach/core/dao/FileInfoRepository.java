package org.apel.show.attach.core.dao;

import java.util.List;

import org.apel.gaia.persist.dao.CommonRepository;
import org.apel.show.attach.core.domain.FileInfoEntity;
import org.springframework.data.jpa.repository.Query;

public interface FileInfoRepository extends CommonRepository<FileInfoEntity, String>{

	/**
	 * 通过业务Id进行查找文件信息
	 * @param businessId
	 * @return
	 */
	public List<FileInfoEntity> findByBusinessId(String businessId);
	
	/**
	 * 通过业务Id查找最大排序值
	 * @param businessId
	 * @return
	 */
	@Query("select max(f.fileSort) from FileInfoEntity f where f.businessId = ?1")
	public Integer findMaxByBusinessId(String businessId);
}
