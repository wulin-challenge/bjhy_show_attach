var firstFileId; //第一次弹出窗口时的文件Id
var businessId; //业务Id
var page=1; 
var rows=1;
var totalPages=0; //总共页
var appVue;
var currentNumber; //当前数据是第几条
$(function(){
	
	var fileId = decodeCode(getUrlParam("fileId"));
	currentNumber = decodeCode(getUrlParam("currentNumber"));
	page = currentNumber;
	firstFileId = fileId;
	getFileInfo();//得到文件信息
	
	appVue = new Vue({
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
	
	
	$("#upPageId").click(function(){
		if(page == 1){
			toastr.warning("当已经是第一页!");
		}else{
			page--;
			openFile(page);
		}
	});
	
	$("#downPageId").click(function(){
		if(totalPages != 0 && page == totalPages){
			toastr.warning("当已经是最后一页!");
		}else{
			page++;
			openFile(page);
		}
	});
	
	$("#upPageId2").click(function(){
		if(page == 1){
			toastr.warning("当已经是第一页!");
		}else{
			page--;
			openFile(page);
		}
	});
	
	$("#downPageId2").click(function(){
		if(totalPages != 0 && page == totalPages){
			toastr.warning("当已经是最后一页!");
		}else{
			page++;
			openFile(page);
		}
	});
});

function openFile(pageNumber){
	appVue.start();
	appVue.percent = 0;
	appVue.progressStatus= "error";
	PlatformUI.ajax({
		type: "get",
		   url:contextPath+'/customerFileInfo/findFileInfo',
		   dataType: "json",
		   data:{page:pageNumber,rows:rows,filters:JSON.stringify(getGridFilters())},
		   async:true,  //async为true表示为异步请求,为false为同步请求
		   afterOperation:function(obj){
			   
			   var upId = obj['items'][0]['id'];
			   totalPages = obj['totalPages'];
			   
				PlatformUI.ajax({
					type: "get",
					   url:contextPath+'/customerFileInfo/showFileContext',
					   dataType: "json",
					   data:{"fileId":upId},
					   async:false,  //async为true表示为异步请求,为false为同步请求
					   afterOperation:function(obj){
						  
						   $("#office_id").empty();
						   $("#office_id").append(obj.htmlContext);
						   appVue.percent = 100;
					   }
				});
			   
//			   alert(upId);
			   console.log(obj);
		   }
	});
}

/**
 * 得到grid的filters
 */
var getGridFilters = function(){
	var rules = [];
	rules.push({"field":"businessId","op":"EQ","data":businessId});
	var filters = {"groupOp":"AND","rules":rules};
	return filters;
}

/**
 * 得到文件信息
 */
function getFileInfo(){
	PlatformUI.ajax({
		type: "get",
		   url:contextPath+'/customerFileInfo/getFileInfo',
		   dataType: "json",
		   data:{"fileId":firstFileId},
		   async:true,  //async为true表示为异步请求,为false为同步请求
		   afterOperation:function(obj){
			   businessId = obj.businessId;
		   }
	});
}

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