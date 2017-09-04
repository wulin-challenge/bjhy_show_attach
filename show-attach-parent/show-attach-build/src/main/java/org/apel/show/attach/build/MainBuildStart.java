package org.apel.show.attach.build;

import org.springframework.context.support.ClassPathXmlApplicationContext;


public class MainBuildStart {

	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/module-*.xml");
		context.start();
		System.out.println("文件服务已启动****");
		System.in.read();
	}
	
//	public static void main(String[] args) throws Exception {
//		PlatformStarter.start(args); 
//		System.out.println("启动成功!");
//	}
	
	
}
