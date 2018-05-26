package org.apel.show.attach.service.loader;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

/**
 * 附件UI加载
 * @author wubo
 *
 */
@Component
public class AttachmentUILoader implements ServletContextAware{
	
	/**
	 * 附件的服务的url地址
	 */
	@Value("${http_attachment_url}")
	private String http_attachment_url;
	
	@Override
	public void setServletContext(ServletContext servletContext) {
		buildAttachmentUIResource(servletContext);
	}
	
	private void buildAttachmentUIResource(ServletContext servletContext){
		String contextPath = servletContext.getContextPath();
		
		String resource = "<script type=\"text/javascript\">var http_attachment_url = '" + http_attachment_url + "' </script>";
		resource += "<script type=\"text/javascript\" src=\"" + contextPath + "/js/common/asyncSubmit.js\"></script>";
		resource += "<script type=\"text/javascript\" src=\"" + contextPath + "/js/common/jquery.form.js\"></script>";
		resource += "<script type=\"text/javascript\" src=\"" + contextPath + "/js-module/indicatorProgressBar/js/radialIndicator.js\"></script>";
		resource += "<script type=\"text/javascript\" src=\"" + contextPath + "/js-module/indicatorProgressBar/dialogIndicator.js\"></script>";
		resource += "<script type=\"text/javascript\" src=\"" + contextPath + "/js/common/attachment.js\"></script>";
		servletContext.setAttribute("attachment", resource);
	}
	
}
