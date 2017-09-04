$(function(){
	
	var fileId = decodeCode(getUrlParam("fileId"));
	
	var appVue = new Vue({
		el: "#vue-progress-id",
		data: function(){
			return {
				percent: 0,
				progressStatus: "error",
				progressVisible: false
			};
		},
		methods: {
			start: function(){
				var context = this;
				this.progressVisible = true;
				setTimeout(function(){
					var timer = setInterval(function(){
						if(context.percent < 100){
							context.percent += 1;
						}else{
							clearInterval(timer);
							context.progressStatus = "success";
							setTimeout(function(){
								context.progressVisible = false;
							}, 500);
						}
					}, 50);
				}, 50);
				
			}
		}
	});
	
	appVue.start();
	
	PlatformUI.ajax({
		type: "get",
		   url:contextPath+'/customerFileInfo/showFileContext',
		   dataType: "json",
		   data:{"fileId":fileId},
		   async:true,  //async为true表示为异步请求,为false为同步请求
		   afterOperation:function(obj){
			  
			   $("#office_id").empty();
			   $("#office_id").append(obj.htmlContext);
			   appVue.percent = 100;
		   }
	});
});

/**
 * 解码字符
 * @param chars
 * @returns
 */
function decodeCode(chars){
	chars = window.decodeURI(chars);
	chars = window.decodeURI(chars);
	return chars;
}

/**
 * 得到当前跳转页面url的参数
 * @param name
 * @returns
 */
function getUrlParam(name){

	var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)"); 

	var r = window.location.search.substr(1).match(reg); 

	if (r!=null) return unescape(r[2]); return null;
} 