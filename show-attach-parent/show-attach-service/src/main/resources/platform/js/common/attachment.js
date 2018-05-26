
(function ($) {
    $.fn.extend({
    	attachment:function(option){
    		var attachment = new Attachment(this,option);
    		attachment.entry();
    		return attachment;
    	}
    });
})(jQuery);

/**
 * 附件组件
 * @currentElement 当前元素,是一个纯粹的html Dom对象
 * option 操作对象
 */
var Attachment = function(currentElement,option){
	var me = this;
	
	/**
	 * 文件显示grid
	 */
	var fileShowGrid = {};
	
	var attachVue = {};
	
	//当前编辑行
	var currentEditId = null;
	
	/**
	 * 入口
	 */
	me.entry = function(){
//		$.ajax({
//			dataType:"jsonp",
//			jsonpCallback:"callback",
//			url:"http://localhost:6688/website/fileInfo/findFileInfo2",
//			type: "post",
//			data:{ id: "休息休息kkk", time: "2pm" },
//			success:function(data, textStatus,jqXHR){
//				console.log(data);
//			}
//		});
		
//		$.getJSON("http://localhost:6688/website/fileInfo/findFileInfo2?jsoncallback=?", 
//				{ id: "John", time: "2pm" },
//		  function(data){
//		    alert("Data Loaded: " + data);
//		  });
//		$.getScript("http://localhost:6688/website/fileInfo/findFileInfo2", 
//				{ id: "John", time: "2pm" },
//				function(data){
//					alert("Data Loaded: " + data);
//				});
//		
		
		
		
		extendOptionParams();//合并option参数
		renderHtml();//渲染html
		readerGrid();
		renderStyle();//渲染css样式
		dealWithEvent();//事件处理方法
	}
	
	/**
	 * 渲染html
	 */
	var renderHtml = function(){
		
		var html = "<div class='first-panel'>";
			html+= "<div id='progressBar'></div>";
			
			html+= "<div class='first-top'>";
			html+= "<div class='first-form' style='"+showButton('upload')+"'>";
			html+= "<form id='upload-form-id'  action='"+option.upload_url+"' method='post' enctype='multipart/form-data'>";
			html+= "<a href='javascript:;' class='first-file'>上传文件 <input type='file' multiple='' name='uploadFile' id='upload-file-id'></a>";
			html+= "</form>";
			html+= "</div>";
			
			html+= "<div class='first-download-zip-div' style='"+showButton('batchDownload')+"'><a id='first-download-zip-id' class='first-download-zip' href='javascript:;'>批量下载</a></div>";
			html+= "<div class='first-del-div' style='"+showButton('batchDelete')+"'><a id='first-batch-del-id' class='first-del' href='javascript:;'>批量删除</a></div>";
			
			html+= "<div class='first-edit-row-div' style='"+showButton('rowEdit')+"'><a id='first-edit-row-div-id' class='first-edit-row-div-class' href='javascript:;'>行编辑</a></div>";
			html+= "<div class='first-save-row-div' style='"+showButton('rowSave')+"'><a id='first-save-row-div-id' class='first-save-row-div-class' href='javascript:;'>行保存</a></div>";
			html+= "<div class='first-cancel-row-div' style='"+showButton('rowCancel')+"'><a id='first-cancel-row-div-id' class='first-cancel-row-div-class' href='javascript:;'>行取消</a></div>";
			
			html+= "</div>";
			
			html+= "<div class='first-bottom'>";
			html+= "<table id='upload-table-List'></table>";
			html+= "<div id='upload-table-pager'></div>";
			html+= "</div>";
			html+= "</div>";
			$(currentElement).empty();
		$(currentElement).append(html);
	}
	
	/**
	 * 渲染css样式
	 */
	var renderStyle = function(){
		
		$(".first-panel").css({"width":option.width,"height":option.height,"border":"1px solid #E4E4E4"});
		$(".first-top").css({"float":"left","clear":"both","width":"100%","height":"40px","background-color": "while"});
		$(".first-bottom").css({"clear":"both","width":"100%","height":getPercentOfPX(option.height,0.89),"background-color": "while"});
		
		//这是上传按钮的样式,"line-height":getPercentOfPX(option.height,0.05)
		
		$(".first-form").css({"float":"left","width":getPercentOfPX(option.width,0.1285)});
		
		$(".first-file").css({"text-align":"center","width":option.upload_button_width+"px","height":option.upload_button_height+"px","font-size":"12px","position":"relative","display":"inline-block","background":"#D0EEFF","border":"1px solid #99D3F5","border-radius":"5px","margin-top":"7px","padding":""+getPercentOfPX(option.height,0.01)+" "+getPercentOfPX(option.width,0.01714)+"","overflow":"hidden","color":"#1E88C7","text-decoration":"none","text-indent":"0"});
		$(".first-file input").css({"position":"absolute","font-size":"12px","right":"0","top":"0","opacity":"0"});
		$(".first-file:hover").css({"background":"#AADFFD","border-color":"#78C3F3","color":"#004974","text-decoration":"none"});
		
		$(".first-download-zip-div").css({"float":"left","height":getPercentOfPX(option.height,0.12),"width":getAndWidth(option.download_zip_button_width,30)+"px"});
		$(".first-download-zip").css({"cursor":"pointer","width":option.download_zip_button_width+"px","height":+option.download_zip_button_height+"px","font-size":"12px","display": "inline-block","position":"relative","display":"inline-block","background":"#D0EEFF","border":"1px solid #99D3F5","border-radius":"5px","margin-top":"7px","padding":""+getPercentOfPX(option.height,0.01)+" "+getPercentOfPX(option.width,0.01714)+"","overflow":"hidden","color":"#1E88C7","text-decoration":"none","text-indent":"0"});
		
		$(".first-del-div").css({"float":"left","height":getPercentOfPX(option.height,0.12),"width":getAndWidth(option.batch_delete_button_width,30)+"px"});
		$(".first-del").css({"cursor":"pointer","width":option.batch_delete_button_width+"px","height":+option.batch_delete_button_height+"px","font-size":"12px","display": "inline-block","position":"relative","display":"inline-block","background":"#D0EEFF","border":"1px solid #99D3F5","border-radius":"5px","margin-top":"7px","padding":""+getPercentOfPX(option.height,0.01)+" "+getPercentOfPX(option.width,0.01714)+"","overflow":"hidden","color":"#1E88C7","text-decoration":"none","text-indent":"0"});
		
		$(".first-edit-row-div").css({"float":"left","height":getPercentOfPX(option.height,0.12),"width":getAndWidth(option.row_edit_button_width,30)+"px"});
		$(".first-edit-row-div-class").css({"cursor":"pointer","width":option.row_edit_button_width+"px","height":+option.row_edit_button_height+"px","font-size":"12px","display": "inline-block","position":"relative","display":"inline-block","background":"#D0EEFF","border":"1px solid #99D3F5","border-radius":"5px","margin-top":"7px","padding":""+getPercentOfPX(option.height,0.01)+" "+getPercentOfPX(option.width,0.01714)+"","overflow":"hidden","color":"#1E88C7","text-decoration":"none","text-indent":"0"});
		
		$(".first-save-row-div").css({"float":"left","height":getPercentOfPX(option.height,0.12),"width":getAndWidth(option.row_save_button_width,30)+"px"});
		$(".first-save-row-div-class").css({"cursor":"pointer","width":option.row_save_button_width+"px","height":+option.row_save_button_height+"px","font-size":"12px","display": "inline-block","position":"relative","display":"inline-block","background":"#D0EEFF","border":"1px solid #99D3F5","border-radius":"5px","margin-top":"7px","padding":""+getPercentOfPX(option.height,0.01)+" "+getPercentOfPX(option.width,0.01714)+"","overflow":"hidden","color":"#1E88C7","text-decoration":"none","text-indent":"0"});
		
		$(".first-cancel-row-div").css({"float":"left","height":getPercentOfPX(option.height,0.12),"width":getAndWidth(option.row_cancel_button_width,30)+"px"});
		$(".first-cancel-row-div-class").css({"cursor":"pointer","width":option.row_cancel_button_width+"px","height":+option.row_cancel_button_height+"px","font-size":"12px","display": "inline-block","position":"relative","display":"inline-block","background":"#D0EEFF","border":"1px solid #99D3F5","border-radius":"5px","margin-top":"7px","padding":""+getPercentOfPX(option.height,0.01)+" "+getPercentOfPX(option.width,0.01714)+"","overflow":"hidden","color":"#1E88C7","text-decoration":"none","text-indent":"0"});
		
	}
	
	/**
	 * 默认的参数
	 * width : 最外层的div的宽度
	 * height : 最外层的div的高度
	 * upload_button_width:上传按钮的宽度
	 * upload_button_height:上传按钮的高度
	 * batch_delete_button_width:批量删除按钮的宽度
	 * batch_delete_button_height:批量删除按钮的高度
	 * userId:用户Id
	 * businessId : 业务Id
	 * upload_url:上传的url
	 * buttonAuthority:按钮的显示权限
	 * 
	 */
	var defaultParams = function(){
		/**
		 * url:上传的url
		 */
		var defaultOptions = {
				width:700,
				height:400,
				upload_button_width:"60", //上传按钮
				upload_button_height:"16",
				batch_delete_button_width:"60", //批量删除按钮
				batch_delete_button_height:"16",
				batch_download_button_width:"60", //批量下载按钮
				batch_download_button_height:"16",
				download_zip_button_width:"60", //打包下载按钮
				download_zip_button_height:"16",
				row_edit_button_width:"39.99", //行编辑按钮
				row_edit_button_height:"16",
				row_save_button_width:"39.99", //行保存
				row_save_button_height:"16",
				row_cancel_button_width:"39.99", //行取消
				row_cancel_button_height:"16",
				userId:"",
				businessId:"",
				upload_url:getHttpContextPath(false) + "fileUpload",
				delete_single_url:getHttpContextPath(false),
				delete_batch_url:getHttpContextPath(false),
				download_url: getHttpContextPath(true) + "downloadFile",
				download_zip_url: getHttpContextPath(true) + "downloadZipFile",
				buttonAuthority:['upload','download','showFile','batchDelete','batchDownload','delete','rowEdit','rowSave','rowCancel'],
				showEventCallback:function(showParam){return true}//预览事件回调,表示打开自身文件,false:表示不打开
		};
		return defaultOptions;
	}
	
	/**
	 * 得到http的上下文路径
	 * @param isRemotePath : 是否强制使用远程路径 
	 */
	var getHttpContextPath = function(isRemotePath){
		if(!isRemotePath && isIe9()){
			return contextPath+"/nativeFileInfo/";
		}
		return http_attachment_url+"/customerFileInfo/";
	}
	
	/**
	 * 是否为ie9 浏览器 ,true表示是,false:表示不是
	 */
	var isIe9 = function(){
		if ((navigator.userAgent.indexOf('MSIE') >= 0) 
				&& (navigator.userAgent.indexOf('Opera') < 0)){
				var userAgent = navigator.userAgent;
				var msieIndex = userAgent.indexOf('MSIE');
				userAgent = userAgent.substring(msieIndex);
				userAgent = userAgent.substring(0,userAgent.indexOf(";"));
				var userAgentArray = userAgent.split(" ");
				if(userAgentArray.length == 2){
					var ieVersion = parseFloat(userAgentArray[1]);
					if(ieVersion<=9){ //小于ie9的版本都返回true
						return true;
					}
				}
			}
		return false;
	}
	
	/**
	 * 显示案例
	 */
	var showButton = function(button){
		var ba = option.buttonAuthority;
		
		for(i=0;i<ba.length;i++){
			if(button == ba[i]){
				return "display:inline";
			}
		}
		return "display:none";
	}
	
	/**
	 * 合并option参数
	 */
	var extendOptionParams = function(){
		if(option == null || option == "" || option == undefined){
			option = {}
		}
		option = $.extend(defaultParams(), option);
	}
	
	/**
	 * 得到百分比数
	 */
	var getPercent = function(len,percent){
		return len*percent;
	}
	
	/**
	 * 得到百分比数后加 px
	 */
	var getPercentOfPX = function(len,percent){
		return (len*percent)+"px";
	}
	
	/**
	 * 将两个数进行相加
	 */
	var getAndWidth = function(width1,width2){
		width1 = parseFloat(width1);
		width2 = parseFloat(width2);
		var width = width1+width2;
		return width;
	}
	
	/**
	 * 得到grid的filters
	 */
	var getGridFilters = function(){
		var rules = [];
		rules.push({"field":"businessId","op":"EQ","data":option.businessId});
		var filters = {"groupOp":"AND","rules":rules};
		return filters;
	}
	
	/**
	 * 渲染grid
	 */
	var readerGrid = function(){
		
		var jqGrid = {
				url: getHttpContextPath(false) + "findFileInfo",
				postData: {filters:JSON.stringify(getGridFilters())},
				datatype: "json",
		        width:getPercent(option.width,0.997),
				height:getPercent(option.height,0.76),
		        mtype: "GET",
//		        cellEdit:true,
		        multiselect: true,
		        colNames: ['id','序号','名称','后缀','大小','上传日期','操作'],
		        colModel: [
		            { name: "id", index:"id",hidden: true},
		            { name: "fileSort",width:72, index:"fileSort", align:"center", sortable: false,editable : true},
		            { name: "fileName",width:187, index:"fileName", align:"center", sortable: false},
		            { name: "fileSuffix",width:72, index:"fileSuffix", align:"center", sortable: false},
		            { name: "fileSize",width:72, index:"fileSize", align:"center", sortable: false},
		            { name: "uploadTime",width:122, index:"uploadTime",align:"center", searchoptions:{dataInit:PlatformUI.defaultJqueryUIDatePick}, sortable: true ,formatter:"date",formatoptions: { srcformat: "U", newformat: "Y-m-d" }},
		            { name: "customColumn",width:255,  formatter:customFunction, align:"center",search:false,sortable: false}
		            ],
		        pager: "#upload-table-pager", 
		        rowNum: 10,
		        rowList: [10, 20, 30],
		        sortname:"uploadTime",
				sortorder:"desc",
		        viewrecords: true,
		        gridview: true,
		        autoencode: true,
		        gridComplete: function(){
		        	bindGridEvent();
		        }//,
//		        beforeSelectRow: function (rowid, e) {//设置行按钮
//		            var $self = $(this), iCol, cm,
//		                $td = $(e.target).closest("tr.jqgrow>td"),
//		                $tr = $td.closest("tr.jqgrow"),
//		                p = $self.jqGrid("getGridParam");
//
//		            if ($(e.target).is("input[type=checkbox]") && $td.length > 0) {
//		               iCol = $.jgrid.getCellIndex($td[0]);
//		               cm = p.colModel[iCol];
//		               if (cm != null && cm.name === "cb") {
//		                   // multiselect checkbox is clicked
//		                   $self.jqGrid("setSelection", $tr.attr("id"), true ,e);
//		               }
//		            }
//		            return false;
//		        }
		    };
		
		var grid = $("#upload-table-List").jqGrid(jqGrid);
		fileShowGrid = grid;
		return grid;
	}
	
	/**
	 * jqGrid 的按钮自定义函数
	 */
	var customFunction = function(cellvalue, options, rowObject){
		var customStyle = "cursor: pointer;"
	    +"width: 25px;"
	    +"height: 16px;"
	    +"font-size: 12px;"
	    +"display: inline-block;"
	    +"position: relative;"
	    +"border: 1px solid rgb(153, 211, 245);"
	    +"border-radius: 5px;"
	    +"margin-top: 3px;"
	    +"padding: 4px 11.998px;"
	    +"overflow: hidden;"
	    +"color: rgb(30, 136, 199);"
	    +"text-decoration: none;"
	    +"text-indent: 0px;"
	    +"background: rgb(208, 238, 255);"
	    
		var custumHtml = "<a class='first-custom first-custom-download' dataId='"+options.rowId+"' style='"+customStyle+showButton('download')+"' href='javascript:void(0);'>下载</a>&nbsp;";
			custumHtml+= "<a class='first-custom first-custom-showFile' dataId='"+options.rowId+"' style='"+customStyle+showButton('showFile')+"' href='javascript:void(0);'>预览</a>&nbsp;";
			custumHtml+= "<a class='first-custom first-custom-delete' dataId='"+options.rowId+"' style='"+customStyle+showButton('delete')+"' href='javascript:void(0);'>删除</a>";
		return custumHtml;
	};
	
	/**
	 * 事件处理方法
	 */
	var dealWithEvent = function(){
		bindUploadEvent();//绑定上传事件
		bindBatchDeleteAttachEvent();//绑定删除事件
		bindDownloadZipAttachEvent();//绑定打包下载事件
		bindRowEditEvent();//绑定行编辑事件
		bindRowSaveEvent();//绑定行保存事件
		bindRowCancelEvent();//绑定行取消事件
	}
	
	/**
	 * 绑定上传事件
	 */
	var bindUploadEvent = function(){
		$("#upload-file-id").change(function(){
			//判断当前是否有编辑行
			if(currentEditId != null){
				toastr.warning("请请先保存当前编辑行!");
				return null;
			}
			
			fileChangedealWith(); //change事件触发后处理的方法
			bandFileChangeEvent() //绑定file文件的change事件
		});
	}
	
	/**
	 * 绑定grid事件
	 */
	var bindGridEvent = function(){
		bindDownloadAttachEvent();//绑定下载事件
		bindShowFileAttachEvent();//绑定预览事件
		bindDeleteAttachEvent();//绑定删除事件
	}
	
	/**
	 * 绑定file文件的change事件
	 */
	var bandFileChangeEvent = function(){
		var html = $("#upload-form-id").html();
		$("#upload-form-id").empty();
		$("#upload-form-id").append(html);
		$('#upload-file-id').off('change').on('change', function() {//上传
			fileChangedealWith(); //change事件触发后处理的方法
			bandFileChangeEvent() //绑定file文件的change事件
		　　return false;
		});
	}
	
	/**
	 * 专门为Ie9而写的上传
	 */
	var fileChangedealWithOfIe9 = function(){
		if(checkFileSize($("#upload-file-id").get(0))){
			var fileName = $("#upload-file-id").val();
			
			//添加模态层
			$("#progressBar2").remove();
			$("#progressBar").append("<div id='progressBar2'></div>");
			var progress = $("#progressBar2").dialogIndicator({attachmentElement:currentElement});
			progress.progressInterval();
			
			$("#upload-form-id").asyncSubmit({
				data:{"businessId":option.businessId,"userId":option.userId,"fileName":fileName},//原生的spring的 multipart 可以这样传输
				check:function(){
					return true;
				},
				callback:function(data){
					if(data.result == 1){
						progress.setProgressNumber(100);
						cleanAndDestroyDialog(progress);
						fileShowGrid.jqGrid("setGridParam", {
							postData: {filters:JSON.stringify(getGridFilters())},
							page:1
						}).trigger("reloadGrid")
					}
				}
			});
		}
		
	}
	
	/**
	 * 清除模态框
	 */
	var cleanAndDestroyDialog = function(progress){
		$("#progressBar2").remove();
		$("#"+progress.getFullBackGroundDiv()).remove();
		$("#"+progress.getIndicatorProcessDiv()).remove();
		$(".fullBackGroundDiv").empty();
		$(".fullBackGroundDiv").remove();
	}
	
	var fileChangedealWithOfOther = function(){
		if(checkFileSize($("#upload-file-id").get(0))){
			var progress = $("#progressBar").dialogIndicator({attachmentElement:currentElement});
			progress.progressInterval();
			var fileName = $("#upload-file-id").val();
			fileName = encodeCode(fileName);
			$("#upload-form-id").ajaxSubmit({
				url:getHttpContextPath(true) + "fileUpload?businessId="+option.businessId+"&userId="+option.userId+"&fileName="+fileName,
				type:"post",
//			    datatype:"text",  //这个datatype在不能设置,否则会出错
			    contentType: "application/json;charset=utf-8",
				async:true,    
				data:{"businessId":option.businessId,"userId":option.userId,"fileName":fileName},//原生的spring的 multipart 可以这样传输
				success:function(data){
					if(data.result == 1){
						progress.setProgressNumber(100);
						fileShowGrid.jqGrid("setGridParam", {
							postData: {filters:JSON.stringify(getGridFilters())},
							page:1
						}).trigger("reloadGrid")
					}
				},
				beforeSubmit:function(){
					
				},
				error:function(){
					toastr.info("上传失败!");
				}
			})
			
		}
		return;
	}
	
	/**
	 * change事件触发后处理的方法
	 */
	var fileChangedealWith = function(){
		if(isIe9()){
			fileChangedealWithOfIe9();
		}else{
			fileChangedealWithOfOther();
		}
	}
	
	var checkFileSize = function(fileInput){
		var checkResult = true;
		var maxSize = 3000 * 1024 * 1024;
		//火狐
        if (fileInput.files && fileInput.files[0]) {
			if(fileInput.files[0].size>(maxSize)){
				toastr.warning('您上传的文件超过30M，请重新上传!');
				checkResult = false;
				return checkResult;
			}
        } 
        return checkResult;
	}
	
	/**
	 * 绑定下载事件
	 */
	var bindDownloadAttachEvent = function(){
		$(".first-custom-download").click(function(e){
			//判断当前是否有编辑行
			if(currentEditId != null){
				toastr.warning("请请先保存当前编辑行!");
				return null;
			}
			
			var dataId = $(e.target).attr("dataId");
			
			window.location.href = option.download_url+"?fileId="+dataId;
		});
	}
	
	/**
	 * 绑定预览事件
	 */
	var bindShowFileAttachEvent = function(){
		$(".first-custom-showFile").click(function(e){
			//判断当前是否有编辑行
			if(currentEditId != null){
				toastr.warning("请请先保存当前编辑行!");
				return null;
			}
//			
//			 var page = $('#your_grid').getGridParam('page'); // current page
//			    var rows = $('#your_grid').getGridParam('rows'); // rows  
//			    var sidx = $('#your_grid').getGridParam('sidx'); // sidx
//			    var sord = $('#your_grid').getGridParam('sord'); // sord
			
			//行数据Id
			var dataId = $(e.target).attr("dataId");
			
			//当前页
			var page = fileShowGrid.getGridParam('page'); // current page
			
			//当前分页条数
			var rowNum = fileShowGrid.getGridParam('rowNum'); // current page
			
			//当页下是第几条
			var ind = fileShowGrid.getInd(dataId);
			
			//当前数据是第几条
			var currentNumber = ((page-1)*rowNum)+ind;
			
			dataId = encodeCode(dataId);
			currentNumber = encodeCode(currentNumber);
			
			var showParam = {fileId:dataId,currentNumber:currentNumber};
			
			var isOpenFile = option.showEventCallback(showParam);
			if(isOpenFile){
				window.open(getHttpContextPath(true)+'newOfficePage?fileId='+dataId+'&currentNumber='+currentNumber,'_blank');
			}
		});
	}
	
	/**
	 * 编码字符
	 */
	var encodeCode = function(chars){
		chars = window.encodeURI(chars);
		chars = window.encodeURI(chars);
		return chars;
	}
	
	
//	/**
//	 * 绑定删除事件
//	 */
//	var bindDeleteAttachEvent = function(){
//		$(".first-custom-delete").click(function(e){
//			var dataId = $(e.target).attr("dataId");
//			var ids = [dataId];
//			
//			
//			attachVue.$confirm('请确认删除数据?', '提示', {
//				confirmButtonText: '确定',
//				cancelButtonText: '取消',
//				type: 'warning',
//				callback:function(action, instance){
//					if(action == 'confirm'){
//						PlatformUI.ajax({
//							url: getHttpContextPath(true),
//							type: "post",
//							data: {_method:"delete",ids:ids},
//							afterOperation: function(){
//								fileShowGrid.jqGrid("setGridParam", {
//									postData: {filters:JSON.stringify(getGridFilters())},
//									page:1,
//									sortname:"uploadTime",
//									sortorder:"desc",
//								}).trigger("reloadGrid");
//							}
//						});
//					}
//				}
//			});
//		});
//	}
	
	/**
	 * 绑定删除事件
	 */
	var bindDeleteAttachEvent = function(){
		$(".first-custom-delete").click(function(e){
			
			//判断当前是否有编辑行
			if(currentEditId != null){
				toastr.warning("请请先保存当前编辑行!");
				return null;
			}
			
			var dataId = $(e.target).attr("dataId");
			var ids = [dataId];
			$.messager.confirm('操作','是否确认删除',function(r){
				if(r){
					PlatformUI.ajax({
						url: option.delete_single_url,
						type: "post",
						data: {_method:"delete",ids:ids},
						afterOperation: function(){
							fileShowGrid.jqGrid("setGridParam", {
								postData: {filters:JSON.stringify(getGridFilters())},
								page:1,
								sortname:"uploadTime",
								sortorder:"desc",
							}).trigger("reloadGrid");
						}
					});
				}
			});
		});
	}
	
	/**
	 * 绑定打包下载zip事件
	 */
	var bindDownloadZipAttachEvent = function(){
		
		$("#first-download-zip-id").click(function(){
			//判断当前是否有编辑行
			if(currentEditId != null){
				toastr.warning("请请先保存当前编辑行!");
				return null;
			}
			
			var ids = fileShowGrid.jqGrid ('getGridParam', 'selarrrow');
			if(ids.length == 0){
				toastr.warning("当前正在打包下载所有数据!");
				
				window.location.href = option.download_zip_url+"?businessId="+option.businessId+"&fileIds=";
			}else{
				var fileIds = "";
				for(var i=0;i<ids.length;i++){
					if(i==0){
						fileIds = ids[i];
					}else{
						fileIds += ","+ids[i];
					}
				}
				window.location.href = option.download_zip_url+"?businessId="+option.businessId+"&fileIds="+fileIds;
			}
			
//			businessId
			
		});
	}
	
	/**
	 * 绑定批量删除事件
	 */
	var bindBatchDeleteAttachEvent = function(){
		
		$("#first-batch-del-id").click(function(){
			//判断当前是否有编辑行
			if(currentEditId != null){
				toastr.warning("请请先保存当前编辑行!");
				return null;
			}
			
			var ids = fileShowGrid.jqGrid ('getGridParam', 'selarrrow');
			if(ids.length == 0){
				toastr.warning("请至少选择一条要删除的数据!");
				return;
			}
			
			$.messager.confirm('操作','是否确认删除',function(r){
				if(r){
					//批量删除ajax
					PlatformUI.ajax({
				        	url: option.delete_batch_url,
							type: "post",
							data: {_method:"delete",ids:ids},
							afterOperation: function(){
								fileShowGrid.jqGrid("setGridParam", {
									postData: {filters:JSON.stringify(getGridFilters())},
									page:1,
									sortname:"uploadTime",
									sortorder:"desc",
								}).trigger("reloadGrid");
							}
					});
				}
			});
			
		
			
		});
	}
	
	//绑定行编辑事件
	var bindRowEditEvent = function(e){
		$(".first-edit-row-div-class").click(function(e){
			
			//判断当前是否有编辑行
			if(currentEditId != null){
				toastr.warning("请请先保存当前编辑行!");
				return null;
			}
			
			var rowId = fileShowGrid.jqGrid ('getGridParam', 'selarrrow');
			
			if(rowId.length != 1){
				toastr.warning("请有且只有选择一行数据!");
				return;
			}
			
			currentEditId = rowId[0];
			// 选中行实际表示的位置
			fileShowGrid.jqGrid('editRow', rowId);
		});
	}
	
	//绑定行保存事件
	var bindRowSaveEvent = function(e){
		$(".first-save-row-div-class").click(function(e){
			
			fileShowGrid.saveRow(currentEditId); 
			var rowData = fileShowGrid.getRowData(currentEditId);
			saveGridRowData(rowData);//保存行数据
			currentEditId = null;
		});
	}
	
	/**
	 * 保存行数据
	 */
	var saveGridRowData = function(rowData){
		PlatformUI.ajax({
			  url: getHttpContextPath(false) + "updateFileInfoById",
			  type: "post",
			  data:{id:rowData['id'],fileSort:rowData['fileSort']},
			  afterOperation: function(data){
				  me.refreshGrid();
			  }
		  });
	}
	
	//绑定行取消事件
	var bindRowCancelEvent = function(e){
		$(".first-cancel-row-div-class").click(function(e){
			fileShowGrid.saveRow(currentEditId); 
			currentEditId = null;
			 me.refreshGrid();
		});
	}
	
	/**
	 * 刷新grid
	 */
	me.refreshGrid = function(){
		fileShowGrid.jqGrid("setGridParam", {
			postData: {filters:JSON.stringify(getGridFilters())},
			page:1
		}).trigger("reloadGrid")
	}
}
