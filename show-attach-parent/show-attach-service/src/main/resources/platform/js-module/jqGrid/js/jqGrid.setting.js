
$(function(){
	
	var myDefault = {
			jsonReader : { 
				root: "items", 
				page: "currentPage", 
				total: "totalPages", 
				records: "totalRows", 
				repeatitems: true, 
				id: "id",
				cell: "",
				userdata: "userdata"
			}
		}
	jQuery.extend(jQuery.jgrid.defaults, myDefault);

});