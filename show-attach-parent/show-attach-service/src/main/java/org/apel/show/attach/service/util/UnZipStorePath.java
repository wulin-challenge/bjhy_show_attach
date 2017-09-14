package org.apel.show.attach.service.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

/**
 * 将zip进行解压并进行
 * @author Administrator
 *
 */
public class UnZipStorePath {
	
	/**
	 * 跟目录
	 */
	private static String rootDirectory;
	
	/**
	 * 解压的zip更目录目录
	 */
	private static String UN_ZIP_ROOT_DIRECTORY="un_zip_root_directory";
	
	/**
	 * 向磁盘中写入文件并解压返回解压后的目录File对象
	 * @param is
	 */
	@SuppressWarnings("resource")
	public static Map<String,Object> writeDisc(InputStream is,String fileName){
		String unZipDirectory = getUnZipDirectory();
		createUnZipDirectory(unZipDirectory);
		File unZipAfterDirectoryFile = null;
		try {
			String unZipPath = replaceSprit(unZipDirectory+"/"+fileName);
			FileOutputStream fos = new FileOutputStream(unZipPath);
			ReadableByteChannel inChannel = Channels.newChannel(is);
			FileChannel outChannel = fos.getChannel();
		
				//DMA内存操作
			outChannel.transferFrom(inChannel, 0, Long.MAX_VALUE);
			//解压
			String unZipAfterDirectory = getUnZipAfterDirectory(unZipDirectory);
			createUnZipAfterDirectory(unZipAfterDirectory);
			
			ZipUtil.unzip(unZipPath, unZipAfterDirectory);
			
			unZipAfterDirectoryFile = new File(unZipAfterDirectory);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Map<String,Object> returnParams = new HashMap<String,Object>();
		returnParams.put("unZipDirectory", unZipDirectory); //当前解压的目录
		returnParams.put("unZipAfterDirectoryFile", unZipAfterDirectoryFile); //当前解压后的目file 对象
		return returnParams;
	}
	
	/**
	 * 递归对去文件
	 * @param unZipAfterDirectoryFile
	 * @param unZipFileCallBack
	 */
	public static void readDirectory(File unZipAfterDirectoryFile,UnZipFileCallBack unZipFileCallBack){
		
		if(unZipAfterDirectoryFile.isDirectory()){
			File[] listFiles = unZipAfterDirectoryFile.listFiles();
			for (File file : listFiles) {
				readDirectory(file,unZipFileCallBack);
			}
		}else{
			unZipFileCallBack.fileCallBack(unZipAfterDirectoryFile);
		}
	}
	
	/**
	 * 创建解压zip目录
	 */
	private static void createUnZipDirectory(String unZipDirectory){
		File file = new File(unZipDirectory);
		if(!file.exists()){
			file.mkdirs();
		}
	}
	
	/**
	 * 得到要解压的zip目录
	 * @return
	 */
	private static String getUnZipDirectory(){
		String uuid = getUUID();
		String unZipPath = replaceSprit(getRootDirectory()+uuid);//得到要解压的zip路径
		return unZipPath;
	}
	/**
	 * 创建解压后zip目录
	 */
	private static void createUnZipAfterDirectory(String unZipAfterDirectory){
		File file = new File(unZipAfterDirectory);
		if(!file.exists()){
			file.mkdirs();
		}
	}
	
	/**
	 * 得到要解压后的zip目录
	 * @return
	 */
	private static String getUnZipAfterDirectory(String unZipDirectory){
		String uuid = getUUID();
		String unZipAfterDirectory= replaceSprit(unZipDirectory+"/"+uuid);//得到要解压后的zip路径
		return unZipAfterDirectory;
	}
	
	/**
	 * 得到容器的更目录
	 * @param rootDirectory
	 * @return
	 */
	public static String getRootDirectory(){
		if(StringUtils.isEmpty(rootDirectory)){
			loadRootDirectory();
		}
		
		if(StringUtils.isEmpty(rootDirectory)){
			System.out.println("解压根目录不能为空!!");
			return null;
		}
		if(!rootDirectory.endsWith("/")){
			rootDirectory +="/";
		}
		return replaceSprit(rootDirectory);
	}
	

	private static void loadRootDirectory(){
		rootDirectory = CenterProperties.getProperty(UN_ZIP_ROOT_DIRECTORY);//解压zip跟目录
		if(StringUtils.isEmpty(rootDirectory)){
			System.out.println("根目录不能为空!!!");
		}
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
	 * 以斜杠结束
	 * @param directory
	 * @return
	 */
	private static String spritEnd(String directory){
		if(StringUtils.isEmpty(directory)){
			System.out.println("目录路径是空!!");
			return null;
		}
		if(!directory.endsWith("/")){
			directory +="/";
		}
		return directory;
	}
	
	/**
	 * 替换正斜杠并以正斜杠结束
	 * @param directory
	 * @return
	 */
	public static String replaceSpritAndSriptEnd(String directory){
		return replaceSprit(spritEnd(directory));
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
	 * 得到uuid
	 * @return
	 */
	public static String getUUID(){
		return UUID.randomUUID().toString().replace("-", "").toUpperCase();
	}
	
	/**
	 * 解压后的zip文件回调类
	 * @author wubo
	 *
	 */
	public abstract class UnZipFileCallBack{
		public abstract void fileCallBack(File unZipAfterDirectoryFile);
	}

}
