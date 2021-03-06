package org.apel.show.attach.core.service;

import java.util.List;

import org.apel.gaia.commons.pager.PageBean;
import org.apel.show.attach.core.domain.FileInfoEntity;

public interface FileInfoService {
	
	/**
	 * 保存
	 * @param fileInfo
	 */
	public void store(FileInfoEntity fileInfo);
	
	/**
	 * 更新
	 * @param fileInfo
	 */
	public void update(FileInfoEntity fileInfo);
	
	/**
	 * 通过Id查找
	 * @param id
	 * @return
	 */
	public FileInfoEntity findOne(String id);
	
	/**
	 * 通过业务Id进行查找
	 * @param businessId
	 * @return
	 */
	public List<FileInfoEntity> findByBusinessId(String businessId);
	
	/**
	 * 通过文件 id 集合查询文件
	 * @param ids
	 * @return
	 */
	public List<FileInfoEntity> findByIds(List<String> ids);
	
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
	 * 通过业务Id查找最大排序值
	 * @param businessId
	 * @return
	 */
	public Integer findMaxByBusinessId(String businessId);
	
	public void findFileInfo(PageBean pageBean);	

}
