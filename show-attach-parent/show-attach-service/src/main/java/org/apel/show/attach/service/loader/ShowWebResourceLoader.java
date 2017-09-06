package org.apel.show.attach.service.loader;

import javax.servlet.ServletContext;

import org.apel.show.attach.service.consist.UIConsist;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

@Component
public class ShowWebResourceLoader implements ServletContextAware{
	
	private boolean isCDN=false;
	
	//构造系统app.js资源
	private String buildAppjsResource(String contextPath){
		String jsTempalte = "<script type=\"text/javascript\" src=\"" + contextPath + "/js/platformUI.js\"></script>"
				+ "<script type=\"text/javascript\" src=\"" + contextPath + "/js/date.js\"></script>";
		return jsTempalte;
	}
	
	//构造jquery的资源
	private String buildJqueryResource(String contextPath){
		if(isCDN){
			return "<script type=\"text/javascript\" src=\"http://cdn.bootcss.com/jquery/1.11.1/jquery.min.js\"></script>";
		}else{
			return "<script type=\"text/javascript\" src=\"" + contextPath + "/jquery/jquery-1.11.3.min.js\"></script>";
		}
		
	}
	
	//构造toast的资源
	private String buildToastrResource(String contextPath){
		String css;
		String js;
		String setting_js;
		if(isCDN){
			css ="<link href=\"http://cdn.bootcss.com/toastr.js/2.1.3/toastr.min.css\" rel=\"stylesheet\"/>";
			js = "<script src=\"http://cdn.bootcss.com/toastr.js/2.1.3/toastr.min.js\" type=\"text/javascript\"></script>";
			setting_js = "<script src=\"" + contextPath + "/js-module/toast/toastr.setting.js\" type=\"text/javascript\"></script>";
			return css + js + setting_js;
		}else{
			css ="<link href=\"" + contextPath + "/js-module/toast/toastr.css\" rel=\"stylesheet\"/>";
			js = "<script src=\"" + contextPath + "/js-module/toast/toastr.js\" type=\"text/javascript\"></script>";
			setting_js = "<script src=\"" + contextPath + "/js-module/toast/toastr.setting.js\" type=\"text/javascript\"></script>";
			return css + js + setting_js;
		}
		
	}
	
	//构造zTree的资源
	private String buildZtreeResource(String contextPath){
		String css;
		String js;
		if(isCDN){
			css = "<link href=\"http://cdn.bootcss.com/zTree.v3/3.5.24/css/zTreeStyle/zTreeStyle.min.css\" rel=\"stylesheet\"/>";
			js = "<script src=\"http://cdn.bootcss.com/zTree.v3/3.5.27/js/jquery.ztree.all.min.js\" type=\"text/javascript\"></script>";
			return css + js;
		}else{
			css = "<link href=\"" + contextPath + "/js-module/zTree/css/zTreeStyle/zTreeStyle.css\" rel=\"stylesheet\"/>";
			js = "<script src=\"" + contextPath + "/js-module/zTree/js/jquery.ztree.all-3.5.min.js\" type=\"text/javascript\"></script>";
			return css + js;
		}
	}
	
	//构造jqGrid资源 
	private String buildJqGridResource(String contextPath){
		String multiSelectPluginCss = "<link href=\"" + contextPath + "/js-module/jqGrid/plugins/ui.multiselect.css\" rel=\"stylesheet\"/>";
		String multiSelectPluginJs = "<script src=\"" + contextPath + "/js-module/jqGrid/plugins/ui.multiselect.js\" type=\"text/javascript\"></script>";
		String theme_css = "<link href=\"" + contextPath + "/js-module/jquery-ui/jquery-ui.theme.min.css\" rel=\"stylesheet\"/>";
		String css = "<link href=\"" + contextPath + "/js-module/jqGrid/css/ui.jqgrid.css\" rel=\"stylesheet\"/>";
		String local_js = "<script src=\"" + contextPath + "/js-module/jqGrid/js/i18n/grid.locale-cn.js\" type=\"text/javascript\"></script>";
		String js = "<script src=\"" + contextPath + "/js-module/jqGrid/js/jquery.jqGrid.js\" type=\"text/javascript\"></script>";
		String setting_js = "<script src=\"" + contextPath + "/js-module/jqGrid/js/jqGrid.setting.js\" type=\"text/javascript\"></script>";
		String dateSearchabaleDeal_js = "<script src=\"" + contextPath + "/js-module/jqGrid/plugins/dateSearchabaleDeal.js\" type=\"text/javascript\"></script>";
		return multiSelectPluginCss + multiSelectPluginJs + theme_css + css + local_js + js + setting_js+dateSearchabaleDeal_js;
	}
	
	//构造easyUI资源
	private String buildEasyUIResource(String contextPath){
		String theme_css = "<link href=\"" + contextPath + "/js-module/easyUI/themes/default/easyui.css\" rel=\"stylesheet\"/>";
		String icon_css = "<link href=\"" + contextPath + "/js-module/easyUI/themes/icon.css\" rel=\"stylesheet\"/>";
		String local_js = "<script src=\"" + contextPath + "/js-module/easyUI/easyui-lang-zh_CN.js\" type=\"text/javascript\"></script>";
		String js = "<script src=\"" + contextPath + "/js-module/easyUI/jquery.easyui.min.js\" type=\"text/javascript\"></script>";
		String validate_js = "<script src=\"" + contextPath + "/js-module/easyUI/easyui.validate.js\" type=\"text/javascript\"></script>";
		return theme_css + icon_css + js + local_js + validate_js;
	}
	
	//构造97Date资源
	private String build97DateResource(String contextPath){
		return "<script type=\"text/javascript\" src=\"" + contextPath + "/js-module/My97DatePicker/WdatePicker.js\"></script>";
	}
	
	//构造bootstrap资源
	private String buildBootstrapResource(String contextPath){
		String css;
		String patch_css;
		String js;
		if(isCDN){
			css = "<link href=\"http://cdn.bootcss.com/bootstrap/3.3.5/css/bootstrap.min.css\" rel=\"stylesheet\"/>";
			patch_css = "<link href=\"" + contextPath + "/js-module/bootstrap/css/bootstrap.patch.css\" rel=\"stylesheet\"/>";
			js = "<script type=\"text/javascript\" src=\"http://cdn.bootcss.com/bootstrap/3.3.5/js/bootstrap.min.js\"></script>";
			return css + patch_css + js;
		}else{
			css = "<link href=\"" + contextPath + "/js-module/bootstrap/css/bootstrap-yeti.min.css\" rel=\"stylesheet\"/>";
			patch_css = "<link href=\"" + contextPath + "/js-module/bootstrap/css/bootstrap.patch.css\" rel=\"stylesheet\"/>";
			js = "<script type=\"text/javascript\" src=\"" + contextPath + "/js-module/bootstrap/js/bootstrap.min.js\"></script>";
			return css + patch_css + js;
		}
	}
	
	//构造bootstrap validate资源
	private String buildBootstrapValidateResource(String contextPath){
		String css;
		String js;
		String js_18n;
		if(isCDN){
			css = "<link href=\"http://cdn.bootcss.com/bootstrap-validator/0.5.3/css/bootstrapValidator.min.css\" rel=\"stylesheet\"/>";
			js = "<script type=\"text/javascript\" src=\"http://cdn.bootcss.com/bootstrap-validator/0.5.3/js/bootstrapValidator.min.js\"></script>";
			js_18n = "<script type=\"text/javascript\" src=\"http://cdn.bootcss.com/bootstrap-validator/0.5.3/js/language/zh_CN.min.js\"></script>"; 
		}else{
			css = "<link href=\"" + contextPath + "/js-module/bootstrap-validate/css/bootstrapValidator.min.css\" rel=\"stylesheet\"/>";
			js = "<script type=\"text/javascript\" src=\"" + contextPath + "/js-module/bootstrap-validate/js/bootstrapValidator.js\"></script>";
			js_18n = "<script type=\"text/javascript\" src=\"" + contextPath + "/js-module/bootstrap-validate/js/language/zh_CN.js\"></script>";
		}
		return css + js + js_18n;
	}
	
	//构造jqueryUI资源
	private String buildJqueryUI(String contextPath){
		String css = "<link href=\"" + contextPath + "/js-module/jquery-ui/jquery-ui.css\" rel=\"stylesheet\"/>";
		String js = "<script type=\"text/javascript\" src=\"" + contextPath + "/js-module/jquery-ui/jquery-ui.js\"></script>";
		return css + js;
	} 
	
	//构造系统ajaxUpload资源
	private String buildAjaxUpload(String contextPath){
		return "<script type=\"text/javascript\" src=\"" + contextPath + "/js-module/ajaxUpload/jquery.ajaxfileupload.js\"></script>";
	}
	
	//构造pjax资源
	private String buildPjax(String contextPath){
		String pjax_js = "<script type=\"text/javascript\" src=\"" + contextPath + "/js-module/noprogress/jquery.pjax.js\"></script>";
		String noprogress_css = "<link href=\"" + contextPath + "/js-module/noprogress/nprogress.css\" rel=\"stylesheet\"/>";
		String noprogress_js = "<script type=\"text/javascript\" src=\"" + contextPath + "/js-module/noprogress/nprogress.js\"></script>";
		return pjax_js + noprogress_css + noprogress_js;
	}
	
	//构造系统layer.js资源
	private String buildLayerjsResource(String contextPath){
		String jsTempalte;
		if(isCDN){
			jsTempalte = "<script type=\"text/javascript\" src=\"http://cdn.bootcss.com/layer/3.0.1/layer.min.js\"></script>";
		}else{
			jsTempalte = "<script type=\"text/javascript\" src=\"" + contextPath + "/js-module/layer/layer.js\"></script>";
		}
		return jsTempalte;
	}
	
	//构造系统layer.js资源
	private String buildPnotifyResource(String contextPath){
		String css1;
		String css2;
		String js1;
		String js2;
		if(isCDN){
			css1 = "<link href=\"http://cdn.bootcss.com/animate.css/3.5.1/animate.min.css\" rel=\"stylesheet\"/>";
			css2 = "<link href=\"http://cdn.bootcss.com/pnotify/3.0.0/pnotify.min.css\" rel=\"stylesheet\"/>";
			js1 = "<script type=\"text/javascript\" src=\"http://cdn.bootcss.com/pnotify/3.0.0/pnotify.min.js\"></script>";
			js2 = "<script type=\"text/javascript\" src=\"" + contextPath + "/js-module/pnotify/js/toast.js\"></script>";
			return css1 + css2 + js1 + js2;
		}else{
			css1 = "<link href=\"" + contextPath + "/js-module/pnotify/css/animate.css\" rel=\"stylesheet\"/>";
			css2 = "<link href=\"" + contextPath + "/js-module/pnotify/css/pnotify.custom.min.css\" rel=\"stylesheet\"/>";
			js1 = "<script type=\"text/javascript\" src=\"" + contextPath + "/js-module/pnotify/js/pnotify.custom.min.js\"></script>";
			js2 = "<script type=\"text/javascript\" src=\"" + contextPath + "/js-module/pnotify/js/toast.js\"></script>";
			return css1 + css2 + js1 + js2;
		}
	}
	
	//构造bootstrap bootbox
	private String buildBootboxResource(String contextPath) {
		String js;
		if(isCDN){
			js = "<script type=\"text/javascript\" src=\"http://cdn.bootcss.com/bootbox.js/4.4.0/bootbox.min.js\"></script>";
		}else{
			js = "<script type=\"text/javascript\" src=\"" + contextPath + "/js-module/bootbox/bootbox.min.js\"></script>";
		}
		return js;
	}

	//构造bootstrap table
	private String buildBootstrapTableResource(String contextPath) {
		String css;
		String js;
		String i18n_js;
		if(isCDN){
			css ="<link href=\"http://cdn.bootcss.com/bootstrap-table/1.11.0/bootstrap-table.css\" rel=\"stylesheet\"/>";
			js = "<script src=\"http://cdn.bootcss.com/bootstrap-table/1.11.0/bootstrap-table.min.js\" type=\"text/javascript\"></script>";
			i18n_js = "<script src=\"http://cdn.bootcss.com/bootstrap-table/1.11.0/locale/bootstrap-table-zh-CN.js\"></script>";
			return css + js + i18n_js;
		}else{
			css ="<link href=\"" + contextPath + "/js-module/bootstrap-table/bootstrap-table.css\" rel=\"stylesheet\"/>";
			js = "<script src=\"" + contextPath + "/js-module/bootstrap-table/bootstrap-table.js\" type=\"text/javascript\"></script>";
			i18n_js = "<script src=\"" + contextPath + "/js-module/bootstrap-table/bootstrap-table-zh-CN.js\" type=\"text/javascript\"></script>";
			return css + js + i18n_js;
		}
	}
	
	//构建jquery validation engine
	private String buildJqValidationEngineResource(String contextPath) {
		String css;
		String js;
		String i18n_js;
		if(isCDN){
			css ="<link href=\"http://cdn.bootcss.com/jQuery-Validation-Engine/2.6.4/validationEngine.jquery.min.css\" rel=\"stylesheet\"/>";
			js = "<script src=\"http://cdn.bootcss.com/jQuery-Validation-Engine/2.6.4/jquery.validationEngine.min.js\" type=\"text/javascript\"></script>";
			i18n_js = "<script src=\"http://cdn.bootcss.com/jQuery-Validation-Engine/2.6.4/languages/jquery.validationEngine-zh_CN.min.js\"></script>";
			return css + js + i18n_js;
		}else{
			css ="<link href=\"" + contextPath + "/js-module/jquery-validation-engine/validationEngine.jquery.css\" rel=\"stylesheet\"/>";
			js = "<script src=\"" + contextPath + "/js-module/jquery-validation-engine/jquery.validationEngine-zh_CN.js\" type=\"text/javascript\"></script>";
			i18n_js = "<script src=\"" + contextPath + "/js-module/jquery-validation-engine/jquery.validationEngine.js\" type=\"text/javascript\"></script>";
			return css + js + i18n_js;
		}
	}
	
	//构建vue.js
	private String buildVueResource(String contextPath) {
		return "<script type=\"text/javascript\" src=\"" + contextPath + "/js-module/vue/vue.js\"></script>";
	}
	
	//构建element-ui
	private String buildEleResource(String contextPath) {
		String css ="<link href=\"" + contextPath + "/js-module/ele/index.css\" rel=\"stylesheet\"/>";
		String js = "<script src=\"" + contextPath + "/js-module/ele/index.js\" type=\"text/javascript\"></script>";
		return css + js;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		String contextPath = servletContext.getContextPath();
		servletContext.setAttribute(UIConsist.JQUERY,buildJqueryResource(contextPath));
		servletContext.setAttribute(UIConsist.TOAST,buildToastrResource(contextPath));
		servletContext.setAttribute(UIConsist.ZTREE,buildZtreeResource(contextPath));
		servletContext.setAttribute(UIConsist.JQGRID,buildJqGridResource(contextPath));
		servletContext.setAttribute(UIConsist.EASYUI,buildEasyUIResource(contextPath));
		servletContext.setAttribute(UIConsist.DATE97,build97DateResource(contextPath));
		servletContext.setAttribute(UIConsist.BOOTSTRAP,buildBootstrapResource(contextPath));
		servletContext.setAttribute(UIConsist.JQUERY_UI,buildJqueryUI(contextPath));
		servletContext.setAttribute(UIConsist.APP_JS,buildAppjsResource(contextPath));
		servletContext.setAttribute(UIConsist.AJAX_UPLOAD,buildAjaxUpload(contextPath));
		servletContext.setAttribute(UIConsist.BOOTSTRAP_VALIDATE,buildBootstrapValidateResource(contextPath));
		servletContext.setAttribute(UIConsist.PJAX,buildPjax(contextPath));
		servletContext.setAttribute(UIConsist.LAYER_JS,buildLayerjsResource(contextPath));
		servletContext.setAttribute(UIConsist.PNOTIFY,buildPnotifyResource(contextPath));
		servletContext.setAttribute(UIConsist.BOOTSTRAP_TABLE,buildBootstrapTableResource(contextPath));
		servletContext.setAttribute(UIConsist.BOOTBOX,buildBootboxResource(contextPath));
		servletContext.setAttribute(UIConsist.JQUERY_VALIDATION_ENGINE,buildJqValidationEngineResource(contextPath));
		servletContext.setAttribute(UIConsist.VUE,buildVueResource(contextPath));
		servletContext.setAttribute(UIConsist.ELEMENT_UI,buildEleResource(contextPath));
	}


}
