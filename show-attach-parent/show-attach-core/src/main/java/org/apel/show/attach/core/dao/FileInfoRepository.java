package org.apel.show.attach.core.dao;

import java.util.List;

import org.apel.gaia.persist.dao.CommonRepository;
import org.apel.show.attach.core.domain.FileInfoEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
	
	/**
	 * 通过 id集合查询文件
	 * @param ids
	 * @return
	 */
	@Query("select f from FileInfoEntity f where f.id in(:ids)")
	public List<FileInfoEntity> findByIds(@Param("ids") List<String> ids);
}
