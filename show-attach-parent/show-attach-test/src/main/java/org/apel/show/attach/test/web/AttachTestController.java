package org.apel.show.attach.test.web;

import java.util.List;

import org.apel.gaia.commons.jqgrid.QueryParams;
import org.apel.gaia.commons.pager.PageBean;
import org.apel.gaia.util.jqgrid.JqGridUtil;
import org.apel.show.attach.service.domain.FileInfo;
import org.apel.show.attach.service.service.FileInfoProviderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.dubbo.config.annotation.Reference;

@Controller
@RequestMapping("/attachTest")
public class AttachTestController {
	
	@Reference(timeout=30000)
	private FileInfoProviderService fileInfoProviderService;
	
	@RequestMapping(value="index",method={RequestMethod.GET,RequestMethod.POST})
	public String index(){
		System.out.println();
//		List<FileInfo> findByBusinessId = fileInfoProviderService.findByBusinessId("111111");
//		List<FileInfo> findFileByBusinessId = fileInfoProviderService.findFileByBusinessId("111111");
//		
//		String filters = "{\"groupOp\":\"AND\",\"rules\":[{\"field\":\"businessId\",\"op\":\"EQ\",\"data\":\"111111\"}]}";
//		
//		QueryParams queryParams = new QueryParams();
//		queryParams.setFilters(filters);
//		queryParams.setNd("1504590288083");
//		
//		JqGridUtil.getPageBean(queryParams);
//		PageBean pageBean = JqGridUtil.getPageBean(queryParams);
//		PageBean findFileInfo = fileInfoProviderService.findFileInfo(pageBean);
//		
//		System.out.println();
		return "index";
	}

}
