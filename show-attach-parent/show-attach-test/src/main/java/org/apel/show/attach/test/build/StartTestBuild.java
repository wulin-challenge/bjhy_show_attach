package org.apel.show.attach.test.build;

import org.apel.gaia.container.boot.PlatformStarter;

public class StartTestBuild {
	
	public static void main(String[] args) {
		PlatformStarter.start(args);
		System.out.println("文件服务test-web启动成功!!");
	}

}
