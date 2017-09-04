package org.apel.show.attach.provider.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class FileServerStart {
	
	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/module-*.xml");
		context.start();
		System.out.println("文件服务已启动****");
		System.in.read();
	}

}
