$(function(){
//	var attachment = $("#attachId").attachment({businessId:"111111",
////		download_url: contextPath + "/attachTest/index",
////		delete_single_url:http_attachment_url + "/customerFileInfo1",
////		delete_batch_url:http_attachment_url + "/customerFileInfo1"
//	});
	
	var businessId = $("#businessInputId").val();
	
	var attachment = $("#attachId").attachment({
		businessId:businessId,
		operationCallback:function(operationParams){
			operationParams = JSON.stringify(operationParams);
			alert(operationParams);
//			var fileInfoList = attachment.findByBusinessId(attachment.getBusinessId());
//			var fileInfoList = attachment.findFileById(operationParams.returnParams.upload);
//			alert(JSON.stringify(fileInfoList));
		}
	});
	
	$("#businessSearchId").click(function(){
		$("#attachId").attachment({businessId:$("#businessInputId").val()});
	});
	
	$("#refreshGridId").click(function(e){
		attachment.refreshGrid();
	});
});