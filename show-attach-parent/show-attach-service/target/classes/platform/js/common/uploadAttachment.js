/**
 * 上传附件插件--uploadFile
 * 调用示例：
<新增>：
         $("#uploadDetaisForm").uploadFile({
				        uploadParam:{
				        	businessType:"code_attach_yhtz",//业务类型
					    	filePath:"yhtz",//上传文件的文件夹名
					    	operation:"add"//当前操作
				            }
				  	 });
这里需要获取业务实体保存之后的业务id,然后设置传附件的业务id,
例如：afterOperation: function(data, textStatus,jqXHR){
              if(textStatus=="success"){
            	 toastr.success("新增成功");
				//获取业务id
				$("#businessID").val(data.bussinessId);
              }
			}
后台新增方法的返回值则需要改变：

	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> create(WzbqgzEntity wzbqgzEntity){
		wzbqgzEntity.setDjjg(UserDetailsUtil.getCurrentUserName());
		Date date=new Date();
		wzbqgzEntity.setDjrq(date);
		wzbqgzEntity.setLrj(UserDetailsUtil.getCurrentUserName());
		String bussinessId = wzbqgzEntityService.saveOrUpdate(wzbqgzEntity);
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("bussinessId",bussinessId);
		return map;
	}
<编辑>
		$("#uploadDetaisForm").uploadFile({
	        uploadParam:{
	        	businessType:"code_attach_yhtz",//业务类型
		    	filePath:"yhtz",//上传文件的文件夹名
		    	operation:"edit"    
	        },
	            viewParam:{businessID:rowData.id},
	  	 });
<查看>
$("#uploadDetaisForm").uploadFile({
	        uploadParam:{
	        	businessType:"code_attach_yhtz",//业务类型
		    	filePath:"yhtz",//上传文件的文件夹名
		    	operation:"view"    
	        },
	            viewParam:{businessID:rowData.id},
	            isSelect:false,---不可上传，只能下载
	  	 });
<业务删除操作>
由于引进了附件的功能，所以在删除罪犯数据的时候需要在后台添加删除附件的操作
@Autowired
private UploadAttachmentBizService UploadAttachmentBizService;
UploadAttachmentBizService.deleteAttachment(id);//单条删除
UploadAttachmentBizService.deleteAttachment(ids);//多条删除

 <参数说明>：
   1.businessType  对应数据字典中附件业务类型的代码唯一值，例如：code_attach_yhtz
   2.filePath 上传文件的路径,这个需要在SystemConfigService接口中定义,
     这里调用就是数据字典中的附件业务类型下自己对应模块的代码唯一值例如：code_attach_yhtz中的yhtz即最后一个'_'后的值
   3.isSelect:true---是否可选
   特别注意：由于直接关闭窗口,每次会导致组件未销毁,重复生成,所以需要在你js显示window的方法内添加onClose事件:
 *  示例：
 *  $('#divID').window({
		title:"XXXX",
	    modal:true,
	    onOpen:function(){
	    	$("#criminal_tabs").tabs("select",0);//默认选中第一个tabs
	    },
	    onClose :function(){
	    	$(".fj_main").empty();
	    }
	});
 */
(function ($) {
    $.fn.extend({
    	uploadFile:function(options){
    	 //生成随机数,用来设置image标签的id
          var fileRandom = Math.floor(Math.random()*100000+1);
          //初始化内部默认参数
           var customParmas = {
       		    gridID:"g_"+fileRandom,//上传文件表格ID
       		    formID:"form_"+fileRandom,//表单ID
       		    pager: "pager_"+fileRandom,//分页ID
       		    textfield_fj:"text_"+fileRandom,
       		    currentTag : this,//当前标签对象
       		    uploadForm:"uploadForm_"+fileRandom
           };
    		//默认参数
    		var defaultsParams = {
    			upload_url:contextPath + "/uploadAttachment/uploadFile",//上传文件的URL
    			del_url:contextPath + "/uploadAttachment",//删除文件URL
    			list_url:contextPath + "/uploadAttachment",//查询列表的URL
    			down_url:contextPath + "/uploadAttachment/download",//下载
    		    sortname:"createDate",//排序字段,默认:createDate
    		    sord: "desc",//排序类型：升序，降序 ,默认降序
    		    isSelect:true,
    		    uploadParam:{},//查询参数 json格式
    		    viewParam:{},//查看参数
    		};
    		
     	    //装载默然参数和传人的参数对象
            var options = $.extend(defaultsParams, options); 
            
            
    		//渲染视图
    		var renderView =function(){
    				var html = "";
    			 	html +="<div class='fj_main'>";
    			 	if(options.isSelect){
    			 	html += "<div class='ul1'>";
    			 	html += "<span class='fj_top'>上传附件:</span>";
    			 	html += "<span>";
    			 	html +="<form id='"+customParmas.uploadForm+"' enctype='multipart/form-data' action='"+options.upload_url+"'  method='POST'>";
    			 	//隐藏域
    			 	html += "<input type='hidden' id='businessType' name='businessType' value='"+options.uploadParam.businessType+"' />";
    			 	if(options.uploadParam.operation=="edit"){//edit
    			 	html += "<input type='hidden' id='businessID'   name ='businessID' value='"+options.viewParam.businessID+"'  />";
    			 	}else{//add
    			 	html += "<input type='hidden' id='businessID'   name ='businessID' />";	
    			 	}
    			 	html += "<input type='hidden' id='filePath'   name ='filePath'  value='"+options.uploadParam.filePath+"' />";
    			 	html += "<input type='text' id='"+customParmas.textfield_fj+"' class='mInin2' placeholder='请选择文件'/>";
    			 	html += "<input type='button' class='mInbu1' value='选择文件...'/>";
    			 	html += "<input type='file' name ='logoFile' class='coninf1' id='logoFile'  />";
    			 	html += "<form>";
    			 	html += "</span>";
    			 	html += "</div>";
    			 	}else{//view
    			 	html += "<input type='hidden' id='businessID'   name ='businessID' value='"+options.viewParam.businessID+"'  />";
    			 	}
    			 	html += "<ul class='fj_ul2'>";
    			 	if(options.isSelect){
    			 	html += "<li><a href='javascript:void(0)' class='delete'><span><img src='../images/icon/delete.png'></span>移除附件</a></li>";
    			 	html += "<li><a href='javascript:void(0)' class='download'><span><img src='../images/icon/page_white_put.png'></span>下载附件</a></li>";
    			 	}
    			 	if(options.isSelect){
    			 	html += "<li><a href='javascript:void(0)' class='upload'><span><img src='../images/icon/accept.png' ></span>确认上传</a></li>";
    			 	html += "<li><a href='javascript:void(0)' class='cancel'><span><img src='../images/icon/arrow_refresh.png'></span>重新选择</a></li>";
    			 	}
    			 	html += "</ul>";
    				html += "<div >";
    			 	html+="<table id='"+customParmas.gridID+"'></table> ";
    			 	html+="<div id='"+customParmas.pager+"'></div>";
    				html += "</div>";
    			 	html+="</div>"; 
    			 	$(customParmas.currentTag).prepend(html);
    			 	initGrid();
    			 	bindEvent();
    		};
    		//动态绑定事件
            var bindEvent = function(){
            	//绑定选择文件change事件
            	$("#logoFile").change(function(){
            		//检查文件的格式
            		var checkFileTypeResult = checkFileType($("#logoFile").val());
        		     //判断上传的文件的格式是否正确  
        			if(!checkFileTypeResult){
        				restFileInput();
        			}
        			//检查文件的大小
        			var checkFileSizeResult = checkFileSize($("#logoFile").get(0));
            		if(!checkFileSizeResult){
            			restFileInput();
            		}else{
            			setFileInput();
            		}
            		
            	});
            	//绑定确认上传文件事件
            	$(".upload").click(function(){
            		uploadFile();
            	});
              	//绑定删除上传文件事件
            	$(".delete").click(function(){
            		batchDelFile();
            	});
              	//绑定取消上传文件事件
            	$(".cancel").click(function(){
            		restFileInput();
            	});
              	//绑定下载文件事件
            	$(".download").click(function(){
            		batchDownloadFile();
            	});
            };
    	    //初始化表格
    		var initGrid = function(){
    			  $("#"+customParmas.gridID).jqGrid({
    				url:options.list_url,
    			    postData:initSearchCondition(),
    			    datatype: "json",
  		        	width: 600,
  			        height:200,
  			        multiselect: true,
  			        colNames: ["id","附件名称","操作"],
  			        colModel: [
  			            { name: "id", index:"id",hidden: true},
  			            { name: "fileName", index:"fileName",align:"center",sortable: true},
  			            { name: "customColumn",formatter:actionFormatter, align:"center",search:false,sortable: false}],
  			        pager: "#"+customParmas.pager,
  			        sortname: options.sidx,
  			        sortorder:options.sord,
  			        rowNum: 10,
  			      	rowList: [10, 20, 30],
  			        viewrecords: true,
  			        gridview: true,
  			        autoencode: true,
  			        caption: "附件信息列表",
  			        gridComplete: function(){
  			        	binCompleteEvent();
  			        }
  			    });
    		};
    		
         // 给表格绑定加载完的事件
		  var binCompleteEvent =function(){
	        	 //选中行鼠标变为手型
	              var ids=$("#"+customParmas.gridID).jqGrid('getDataIDs');
		            for(var i = 0; i < ids.length ; i ++){
		               var id = ids[i];
		               $("#"+id).attr("style","cursor:pointer");
		             }
		           
		         //绑定移除事件
		     	$(".del").click(function(e){
	        		var obj = eval("(" + $(e.target).attr("name") + ")");
	        		deleteFile(obj.id);
	        	});
		        //绑定下载事件
		     	$(".downloadFile").click(function(e){
	        		var obj = eval("(" + $(e.target).attr("name") + ")");
	        		downloadFile(obj.id);
	        	});
		   };
    		//自定义列
    		var actionFormatter = function(cellvalue, options, rowObject){
    			var Obj = "{id:" + "\"" + rowObject.id + "\"" + "}";
    			var columnTemplate ="<a href='javascript:void(0)'><img src='../images/icon/page_white_put.png' width='16' height='16' title='下载附件' class='downloadFile' name='"+ Obj +"'></a>" +
    					"<a href='javascript:void(0)'><img src='../images/icon/delete.png' width='16' height='16' title='移除附件' class='del'  name='"+ Obj +"'></a>";
    			return columnTemplate;	
    		};
//******************************************操作上传附件方法区
    	//上传文件
       var uploadFile = function(){
    	   $.messager.confirm('操作提示','确认上传',function(r){
    		   if (r){
    		   var checkResult = $("#logoFile").val();
    	   		if(checkResult){
    			//判断当前文件夹的文件个数
    			var ids  = $("#"+customParmas.gridID).jqGrid('getDataIDs');
    			var fileTotal = ids.length;
    			if(fileTotal > 20){
    				toastr.error("文件个数已超出服务器资源限度,请删除文件后再上传!");
    				return;
    			}
    			//调用上传文件的方法
    			$("#"+customParmas.uploadForm).asyncSubmit(option);
    			/////////////
    			
    			postData1  = initSearchCondition();
    			sortname1 = options.sortname ;
    			sortorder1 = options.sord;
    			customParmas1 = customParmas;
    			
    			//解决上传刷新不出来的情况
    			setTimeout('refreshUploadTable()',500);
    			
    			//重置文件选择框
 	        	var checkResult = checkFileSize($("#logoFile").get(0));
        		if(checkResult){
        			setFileInput();
        			restFileInput();
        			
        		}
    			///////////
    			//进度条
    			$.messager.progress({ 
    			    title: '等待', 
    			    msg: '上传文件中...', 
    			    text: '正在上传文件,这可能需要一会儿....' 
    			});
    			
    			}else{
    				toastr.warning("请选择文件上传!");
    			}
    	   		
    		   }
    		   });
    		};
    	//回调参数
    	var option = {
    			callback:function(data){
        			if(data.result==1){
        				var msg = "";
        				if(data.msg){
        					msg = data.msg;
        				}else{
        					msg = "上传失败!"; 
        				}
         	        		toastr.error(msg);
        				}else if(data.result==0){
        					toastr.success("上传成功!");
        				}
            		   //关闭进度条
     	        	  $.messager.progress('close');
     	        	 isRefresh = true;
    				}
    			};
    		
    	//删除文件
    	//参数说明：id 文件id
    	var deleteFile = function(id){
    			$.messager.confirm('操作提示','确认移除',function(r){
    			    if (r){
    			        ajax({
    						url: options.del_url+"/"+id, 
    						type: "post",
    						data: "_method=delete",
    						afterOperation: function(){
    							//刷新表格
    							refreshUploadGrid();
    						}
    					});
    			    }
    			});
    		};
    	//批量删除文件
    	var batchDelFile = function(){
    			var ids = $("#"+customParmas.gridID).jqGrid ('getGridParam', 'selarrrow');
    			if(ids==undefined||ids.length ==0 ){
    				toastr.warning("请选择一个或多个附件移除!");
    				return;
    			}else{
    			$.messager.confirm('操作','请确认删除数据',function(r){
    		    	if (r){
    			        ajax({
    						url: options.del_url,
    						type: "post",
    						data: {_method:"delete",ids:ids},
    						afterOperation: function(){
    							refreshUploadGrid();
    						}
    					});
    			    }
    			});
    			}
    			
    		};
    	//下载文件
    	var downloadFile = function(id){
    			$.messager.confirm('操作提示','确认下载附件',function(r){
    			    if (r){
    			    	downloadForm(id);
		    			
    			    }
    			});
    		};
    		
    	//下载个文件
    	var  batchDownloadFile = function(){
    			var ids = $("#"+customParmas.gridID).jqGrid ('getGridParam', 'selarrrow');
    			if(ids==undefined ||ids.length ==0||ids.length!=1 ){
    				toastr.warning("请选择一个附件下载!");
    				return;
    			}else{
    			$.messager.confirm('操作','确认下载附件',function(r){
    		      if (r){
    		    	  downloadForm(ids[0]);
    		       }
    			});
    			}
    			
    		};
    	 //表格初始化条件
          var initSearchCondition = function (){
      			var filters = {
      					groupOp : 'AND',
      					rules : [ ],
      					groups:[]
      				};
      			
      			 if(options.uploadParam.businessType!=null||options.uploadParam.businessType!=undefined){
       				 filters.rules.push({field : 'businessType',op : 'eq',data :options.uploadParam.businessType});
				   }
      			 if(options.uploadParam.operation=="view"||options.uploadParam.operation=="edit"){
      				  if(options.viewParam.businessID!=null||options.viewParam.businessID!=undefined){
          			     filters.rules.push({field : 'businessID',op : 'eq',data :options.viewParam.businessID});
      				  }
      			   }else if(options.uploadParam.operation=="add"){
      				 var addBusinessID = $("#businessID").val();
         			  filters.rules.push({field : 'businessID',op : 'eq',data :addBusinessID});
      			   }
      		
      				return {filters:JSON.stringify(filters)};
      		}
    	//下载文件隐藏域表单
    	var downloadForm =function(id){
				var form = $(document.createElement('form')).attr("id", "common_downloadForm")
		    	.attr('action', options.down_url)
                .attr('method','get').css("display", "none");
				$('body').append(form);
				$(document.createElement('input')).attr('type', 'hidden').attr('name','id').attr('value', id).appendTo(form);
				$(form).submit();
    		};
    	//重置fileiput
    	var restFileInput = function(){
    			var file =  $("#"+customParmas.textfield_fj).val();
			    if(file!=null||file!=undefined){
			    	$("#"+customParmas.textfield_fj).val("");
     	        	$("#logoFile").val(""); 
    			 }
    		};
    	//设置fileiput值
    	var setFileInput = function(){
    			var value = $("#logoFile").val();
        		$("#"+customParmas.textfield_fj).val(value);
    		};
    	//检查上传文件的大小
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
        		
    	//判断文件格式
    	var checkFileType = function (file){
				//附件格式
				var arrType = ["xls","xlsx","doc","docx","ppt","pptx","pdf","txt","jpeg","png","zip","rar","jpg"];
    			if(file){
    		        var fileType = file.substring(file.lastIndexOf(".")+1);  
					var  indexof = $.inArray(fileType, arrType);
    		        if(indexof==-1){  
    		        	toastr.warning("上传文件格式错误,目前仅支持xls/xlsx/doc/docx/ppt/pptx/pdf/txt/jpeg/png/zip/rar!");
    		        	return false;
    		        }
    			}
    			return true;
    		};
    	  //刷新表格
    	 var refreshUploadGrid = function (){
    		  $("#"+customParmas.gridID).jqGrid("setGridParam", {
					postData: initSearchCondition(),
					sortname:options.sortname,
					sortorder:options.sord
				 }).trigger("reloadGrid");
    	  }
    	//程序入口
    	renderView();
    		
    	}
    });
})(jQuery);

//处理二次上传列表不刷新列表的问题
var postData1 ;
var sortname1;
var sortorder1;
var customParmas1;
var isRefresh = false; //是否刷新
function refreshUploadTable(){
	if(isRefresh){
		 $("#"+customParmas1.gridID).jqGrid("setGridParam", {
				postData: postData1,
				sortname:sortname1,
				sortorder:sortorder1
			 }).trigger("reloadGrid");
		 isRefresh = false;
	}else{
		setTimeout('refreshUploadTable()',100);
	}
	
	 
}


