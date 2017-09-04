package org.apel.show.attach.core.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 文件信息
 * @author wubo
 *
 */
@Entity
@Table(name="base_file_info")
public class FileInfoEntity implements Serializable {
	
	private static final long serialVersionUID = 8109432845302294738L;

	@Id
	private String id;
	
	/**
	 * 业务Id
	 */
	private String businessId;
	
	/**
	 * 用户Id(特殊情况下准备)
	 */
	private String userId;
	
	/**
	 * 文件名称
	 */
	private String fileName;
	
	/**
	 * 文件的后缀
	 */
	private String fileSuffix;
	
	/**
	 * 文件大小
	 */
	private String fileSize;
	
	/**
	 * 文件的相对路径
	 */
	private String relativePath;
	
	/**
	 * 上传时间
	 */
	private Date uploadTime;
	
	/**
	 * 文件状态
	 */
	private Boolean fileStatus;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileSuffix() {
		return fileSuffix;
	}

	public void setFileSuffix(String fileSuffix) {
		this.fileSuffix = fileSuffix;
	}

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

	public Date getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}

	public Boolean getFileStatus() {
		return fileStatus;
	}

	public void setFileStatus(Boolean fileStatus) {
		this.fileStatus = fileStatus;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	
}
