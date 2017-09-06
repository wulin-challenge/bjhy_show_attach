$(function(){
	var attachment = $("#attachId").attachment({businessId:"111111"});
	
	$("#refreshGridId").click(function(e){
		attachment.refreshGrid();
	});
	
	
});