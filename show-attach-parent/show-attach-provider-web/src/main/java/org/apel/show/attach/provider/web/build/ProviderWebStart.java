package org.apel.show.attach.provider.web.build;

import org.apel.gaia.container.boot.PlatformStarter;

public class ProviderWebStart {
	
	public static void main(String[] args) throws Exception {
		PlatformStarter.start(args); 
		System.out.println("启动成功!");
	}

}
