package org.apel.show.attach.core.base;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

/**
 * 文件存储路径
 * @author wubo
 *
 */
@SuppressWarnings("static-access")
public class FileStorePath {
	
	/**
	 * 存储的跟目录
	 */
	private String rootDirectory;
	
	/**
	 * uuid
	 */
	private String uuid;
	
	/**
	 * 文件名
	 */
	private String fileName;
	
	/**
	 * 文件后缀
	 */
	private String fileSuffix;
	
	private FileStorePath(){}
	
	public FileStorePath(String rootDirectory,String fileName,String fileSuffix){
		this.rootDirectory = rootDirectory;
		this.fileName = fileName;
		this.fileSuffix = fileSuffix;
		this.uuid =  UUID.randomUUID().toString().replace("-", "").toUpperCase();
	}
	
	/**
	 * 得到相对目录
	 * @return
	 */
	private String getRelativeDirectory(){
		String relativePath = "file-store/"+new SimpleDateFormat("yyyy/MM/dd").format(new Date())+"/";
		return replaceSprit(relativePath);
	}
	
	/**
	 * 相对路径
	 * @return
	 */
	public String getRelativePath(){
		return replaceSprit(getRelativeDirectory()+getUuid());
	}
	
	/**
	 * 得到真正的路径
	 * @return
	 */
	public String getReallyDirecotory(){
		String reallyDirectory = getRootDirectory();
		if(!reallyDirectory.endsWith("/")){
			reallyDirectory +="/";
		}
		
		reallyDirectory +=getRelativeDirectory();
		return replaceSprit(reallyDirectory);
	}
	
	public String getReallyPath(){
		return replaceSprit(getReallyDirecotory()+getUuid());
	}
	
	/**
	 * 得到跟路径
	 * @return
	 */
	public String getRootDirectory(){
		String rootDirectory = null;
		if(StringUtils.isEmpty(this.rootDirectory)){
			rootDirectory =  replaceSprit(defaultRootDirectory());
		}else{
			rootDirectory = replaceSprit(this.rootDirectory);
		}
		
		if(!rootDirectory.endsWith("/")){
			rootDirectory +="/";
		}
		return replaceSprit(rootDirectory);
	}
	
	/**
	 * 这是提供给外部随处都可以调用的静态方法,以方便得到真正的跟路径
	 * @param rootDirectory 若为空,则返回的是默认的路径
	 * @return
	 */
	public static String getRootDirectory(String rootDirectory){
		FileStorePath fileStorePath = new FileStorePath();
		if(StringUtils.isEmpty(rootDirectory)){
			rootDirectory = fileStorePath.defaultRootDirectory();
		}
		rootDirectory = fileStorePath.replaceSprit(rootDirectory);
		
		if(!rootDirectory.endsWith("/")){
			rootDirectory +="/";
		}
		return fileStorePath.replaceSprit(rootDirectory);
	}
	
	/**
	 * 替换反斜杠,解决在windows,linux下的路径问题
	 * @param path
	 * @return
	 */
	public static String replaceSprit(String path){
		if(StringUtils.isEmpty(path)){
			return "";
		}
		path = path.replace("\\\\", "/"); //// Java中4个反斜杠表示一个反斜杠
		path = path.replace("\\", "/"); 
		return path;
	}
	
	/**
	 * 得到默认的目录
	 * @return
	 */
	private String defaultRootDirectory(){
		File rootDir = new File(System.getProperty("user.dir"),"office");
		return replaceSprit(rootDir.getAbsolutePath());
	}

	/**
	 * 得到uuid
	 * @return
	 */
	public String getUuid() {
		return uuid;
	}
	
	public String getFileName() {
		return fileName;
	}

	public String getFileSuffix() {
		return fileSuffix;
	}

	/**
	 * 创建目录 创建成功返回true,否则返回false
	 * @param fileDirectory 文件目录
	 * @return
	 */
	public Boolean createDirectory(String fileDirectory){
		
		if(StringUtils.isEmpty(fileDirectory)){
			return false;
		}
		
		File file = new File(fileDirectory);
		//文件目录是否存在,不存在就创建
		if(!file.exists()){
			file.mkdirs();
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 将文件路径变成文件输入流
	 * @param file
	 * @return
	 */
	public static InputStream getInputStreamByFilePath(String filePath){
		File file = new File(filePath);
		return getInputStreamByFile(file);
	}
	
	/**
	 * 将文件路径变成文件输入流以及返回文件大小
	 * @param file
	 * @return
	 */
	public static SimpleFile getInputStreamByFilePathAndSize(String filePath){
		File file = new File(filePath);
		InputStream is = getInputStreamByFile(file);
		long fileSize = file.length();
		
		SimpleFile simpleFile = new FileStorePath().new SimpleFile();
		simpleFile.setIs(is);
		simpleFile.setFileSize(fileSize);
		return simpleFile;
	}
	
	/**
	 * 将文件变成文件输入流
	 * @param file
	 * @return
	 */
	public static InputStream getInputStreamByFile(File file){
		
		//不是文件就返回null
		if(!file.isFile()){
			return null;
		}
		
		try {
			FileInputStream fis= new FileInputStream(file);
			return fis;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//通过byte[]数组得到流
	public static InputStream byteToInputStream(byte[] in){  
        ByteArrayInputStream is = new ByteArrayInputStream(in);  
        return is;
    }
	
	/** 
     * 获得InputStream的byte数组 
     */  
	public static byte[] getBytes(InputStream is) {
		byte[] buffer = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
		byte[] b = new byte[1000];
		try {
			int n = 0;
			while ((n = is.read(b)) != -1) {
				bos.write(b, 0, n);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		buffer = bos.toByteArray();
		return buffer;
	}
	
	/**
	 * 删除某个文件夹及该文件夹下的所有文件及文件夹
	 * @param delpath
	 * @return
	 * @throws Exception
	 */
	public static boolean deleteFile(String delpath) throws Exception{
		try {
			delpath = delpath.replace("\\\\", "/"); //// Java中4个反斜杠表示一个反斜杠
			delpath = delpath.replace("\\", "/"); 
			File file = new File(delpath);
			// 当且仅当此抽象路径名表示的文件存在且 是一个目录时，返回 true
			if (!file.isDirectory()) {
				file.delete();
			} else if (file.isDirectory()) {
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File delfile = new File(delpath + "/" + filelist[i]);
					if (!delfile.isDirectory()) {
						delfile.delete();
						System.out.println(delfile.getAbsolutePath() + "删除文件成功");
					} else if (delfile.isDirectory()) {
						deleteFile(delpath + "/" + filelist[i]);
					}
				}
				System.out.println(file.getAbsolutePath() + "删除成功");
				file.delete();
			}

		} catch (FileNotFoundException e) {
			System.out.println("deletefile() Exception:" + e.getMessage());
		}
		return true;
	}
	
	/**
	 * 格式化文件的大小
	 * @param fileSize
	 * @return
	 */
	public static String formetFileSize(long fileSize) {//转换文件大小
	       DecimalFormat df = new DecimalFormat("#.00");
	       String fileSizeString = "";
	       if (fileSize < 1024) {
	           fileSizeString = df.format((double) fileSize) + "B";
	       } else if (fileSize < 1048576) {
	           fileSizeString = df.format((double) fileSize / 1024) + "K";
	       } else if (fileSize < 1073741824) {
	           fileSizeString = df.format((double) fileSize / 1048576) + "M";
	       } else {
	           fileSizeString = df.format((double) fileSize / 1073741824) +"G";
	       }
	       return fileSizeString;
	    }
	
	
	/**
	 * 简单的文件对象
	 * @author wubo
	 */
	public class SimpleFile{
		
		/**
		 * 文件流
		 */
		private InputStream is;
		
		/**
		 * 文件大小
		 */
		private long fileSize;

		public InputStream getIs() {
			return is;
		}

		public void setIs(InputStream is) {
			this.is = is;
		}

		public long getFileSize() {
			return fileSize;
		}

		public void setFileSize(long fileSize) {
			this.fileSize = fileSize;
		}
	}
	
}
