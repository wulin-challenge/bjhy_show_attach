
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
		attachVue = initVue();
		readerGrid();
		renderStyle();//渲染css样式
		dealWithEvent();//事件处理方法
	}
	
	/**
	 * 初始化vue的组件
	 */
	var initVue = function(){
		var appVue = new Vue({
			el: '.first-panel',
			data: function(){
				return {
					percent: 0,
					progressStatus: null,
					progressVisible: false,
					singleDelete:false,
					batchDelete:false,
					singleDeleteParams:null
				};
			},
			methods: {
				start: function(){
					var context = this;
					context.percent = 0;
					context.progressStatus = null;
					this.progressVisible = true;
					setTimeout(function(){
						var timer = setInterval(function(){
							if(context.percent < 100){
								PlatformUI.ajax({
				        			  url: http_attachment_url + "/customerFileInfo/file_progress",
				        			  type: "get",
				        			  afterOperation: function(data){
				        				  context.percent += data;
				        			  }
				        		  });
								
							}else{
								clearInterval(timer);
								context.progressStatus = "success";
								setTimeout(function(){
									context.progressVisible = false;
								}, 600);
							}
						}, 200);
					}, 500);
					
				},
				/**
				 * 单删除取消
				 */
				singleDeleteCancel:function(){
					this.singleDelete=false;
				},
				
				/**
				 * 单删除确认
				 */
				singleDeleteEnsure:function(){
					var e = this.singleDeleteParams;
					var dataId = $(e.target).attr("dataId");
					var ids = [dataId];
					
					PlatformUI.ajax({
						url: http_attachment_url + "/customerFileInfo",
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
					this.singleDelete=false;
				},
				
				/**
				 * 批量删除取消
				 */
				batchDeleteCancel:function(){
					this.batchDelete=false;
				},
				
				/**
				 * 批量删除确认
				 */
				batchDeleteEnsure:function(){
					var ids = fileShowGrid.jqGrid ('getGridParam', 'selarrrow');
					if(ids.length == 0){
						toastr.warning("请至少选择一条要删除的数据!");
						return;
					}
					
					 PlatformUI.ajax({
				        	url: http_attachment_url + "/customerFileInfo",
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
					this.batchDelete=false;
				}
			}
			});
		return appVue;
	}
	
	/**
	 * 渲染html
	 */
	var renderHtml = function(){
		
		var html = "<div class='first-panel'>";
			html+= "<div class='first-vue'>";
			html+= "<el-dialog title='进度条' v-model='progressVisible' size='tiny' :close-on-click-modal='false' :close-on-press-escape='false' :show-close='false' >";
			html+= "<el-row><el-col :span='8'>&nbsp;</el-col><el-col :span='8'>";
			html+= "<el-progress type='circle' :percentage='percent' :status='progressStatus'></el-progress>";
			html+= "</el-col><el-col :span='8'>&nbsp;</el-col></el-row>";
			html+= "</el-dialog>";
			
			html+= "<el-dialog title='提示' v-model='singleDelete' size='tiny'>";
			html+= "<span>确认删除吗?</span>";
			html+= "<span slot='footer' class='dialog-footer'>";
			html+= "<el-button @click='singleDeleteCancel'>取 消</el-button>";
			html+= "<el-button type='primary' @click='singleDeleteEnsure'>确 定</el-button>";
			html+= "</span>";
			html+= "</el-dialog>";
			
			html+= "<el-dialog title='提示' v-model='batchDelete' size='tiny'>";
			html+= "<span>确认删除吗?</span>";
			html+= "<span slot='footer' class='dialog-footer'>";
			html+= "<el-button @click='batchDeleteCancel'>取 消</el-button>";
			html+= "<el-button type='primary' @click='batchDeleteEnsure'>确 定</el-button>";
			html+= "</span>";
			html+= "</el-dialog>";
			
			html+= "</div>";
			
			html+= "<div class='first-top'>";
			html+= "<div class='first-form' style='"+showButton('upload')+"'>";
			html+= "<form id='upload-form-id'  action='"+option.upload_url+"' method='post' enctype='multipart/form-data'>";
			html+= "<a href='javascript:;' class='first-file'>上传文件 <input type='file' name='uploadFile' id='upload-file-id'></a>";
			html+= "</form>";
			html+= "</div>";
			html+= "<div class='first-del-div' style='"+showButton('batchDelete')+"'><a id='first-batch-del-id' class='first-del' href='javascript:;'>批量删除</a></div>";
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
		
		$(".first-del-div").css({"float":"left","height":getPercentOfPX(option.height,0.12)});
		$(".first-del").css({"cursor":"pointer","width":option.batch_delete_button_width+"px","height":+option.batch_delete_button_height+"px","font-size":"12px","display": "inline-block","position":"relative","display":"inline-block","background":"#D0EEFF","border":"1px solid #99D3F5","border-radius":"5px","margin-top":"7px","padding":""+getPercentOfPX(option.height,0.01)+" "+getPercentOfPX(option.width,0.01714)+"","overflow":"hidden","color":"#1E88C7","text-decoration":"none","text-indent":"0"});
//		$(".el-button").css({"padding":"0px 0px","font-size":"1pt"});
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
				upload_button_width:"59.99",
				upload_button_height:"16",
				batch_delete_button_width:"59.99",
				batch_delete_button_height:"16",
				userId:"",
				businessId:"",
				upload_url:http_attachment_url + "/customerFileInfo/fileUpload",
				buttonAuthority:['upload','download','showFile','batchDelete','delete']
		};
		return defaultOptions;
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
				url: http_attachment_url + "/customerFileInfo/findFileInfo",
				postData: {filters:JSON.stringify(getGridFilters())},
				datatype: "json",
		        width:getPercent(option.width,0.997),
				height:getPercent(option.height,0.76),
		        mtype: "GET",
		        multiselect: true,
		        colNames: ['id','名称','后缀','大小','上传日期','操作'],
		        colModel: [
		            { name: "id", index:"id",hidden: true},
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
		        }
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
	}
	
	/**
	 * 绑定上传事件
	 */
	var bindUploadEvent = function(){
		$("#upload-file-id").change(function(){
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
	 * change事件触发后处理的方法
	 */
	var fileChangedealWith = function(){
		if(checkFileSize($("#upload-file-id").get(0))){
			attachVue.start();
			$("#upload-form-id").ajaxSubmit({
				url:http_attachment_url + "/customerFileInfo/fileUpload",
				type:"post",
			    datatype:"text",  //这个datatype在不能设置,否则会出错
				async:true,    
				data:{"businessId":option.businessId,"userId":option.userId},
				success:function(data){
					data = eval("("+data+")");
					if(data.result == 1){
						attachVue.percent = 100;
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
	
	var checkFileSize = function(fileInput){
		var checkResult = true;
		var maxSize = 30 * 1024 * 1024;
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
			var dataId = $(e.target).attr("dataId");
			
			window.location.href = http_attachment_url + "/customerFileInfo/downloadFile?fileId="+dataId;
		});
	}
	
	/**
	 * 绑定预览事件
	 */
	var bindShowFileAttachEvent = function(){
		$(".first-custom-showFile").click(function(e){
			var dataId = $(e.target).attr("dataId");
			dataId = encodeCode(dataId);
			
			window.open(http_attachment_url+'/customerFileInfo/newOfficePage?fileId='+dataId,'_blank');  
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
//							url: http_attachment_url + "/customerFileInfo",
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
			attachVue.singleDeleteParams = e;
			attachVue.singleDelete=true;
		});
	}
	
	/**
	 * 绑定批量删除事件
	 */
	var bindBatchDeleteAttachEvent = function(){
		
		$("#first-batch-del-id").click(function(){
			
			attachVue.batchDelete=true;
			
//			var ids = fileShowGrid.jqGrid ('getGridParam', 'selarrrow');
//			if(ids.length == 0){
//				toastr.warning("请至少选择一条要删除的数据!");
//				return;
//			}
			//批量删除ajax
//			attachVue.$confirm('请确认删除数据?', '提示', {
//		          confirmButtonText: '确定',
//		          cancelButtonText: '取消',
//		          type: 'warning',
//		          callback:function(action, instance){
//		        	  if(action == 'confirm'){
//		        		  PlatformUI.ajax({
//					        	url: http_attachment_url + "/customerFileInfo",
//								type: "post",
//								data: {_method:"delete",ids:ids},
//								afterOperation: function(){
//									fileShowGrid.jqGrid("setGridParam", {
//										postData: {filters:JSON.stringify(getGridFilters())},
//										page:1,
//										sortname:"uploadTime",
//										sortorder:"desc",
//									}).trigger("reloadGrid");
//								}
//							});
//		        	  }
//		          }
//		     });
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
