$(function(){
	var attachment = $("#attachId").attachment({businessId:"111111",
//		download_url: contextPath + "/attachTest/index",
//		delete_single_url:http_attachment_url + "/customerFileInfo1",
//		delete_batch_url:http_attachment_url + "/customerFileInfo1"
	});
	
	$("#refreshGridId").click(function(e){
		attachment.refreshGrid();
	});
	
	
});