package org.apel.show.attach.build;

import java.io.IOException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ProviderStart {
	
	public static void main(String[] args) throws IOException {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/module-*.xml");
		context.start();
		System.out.println("文件服务已启动****");
		System.in.read();
	}

}
