package org.apel.show.attach.provider.test;

import org.apel.gaia.app.boot.AppStarter;

public class FileServerStart {
	
	public static void main(String[] args) throws Exception {
//		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/module-*.xml");
//		context.start();
//		System.out.println("文件服务已启动****");
//		System.in.read();
		AppStarter.start(args);
//		AppStarter.startWithBlocking(args);
//		PlatformStarter.start(args);
		System.out.println("文件服务已启动****");
		
	}

}
