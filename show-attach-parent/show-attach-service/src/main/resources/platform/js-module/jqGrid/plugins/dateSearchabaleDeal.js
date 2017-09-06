function FieldtypeAddtioner(searchList){
	var me = this;
	var list = searchList;
	this.search=function(){
		var postData = list.jqGrid ('getGridParam', 'postData');
		var filters = postData.filters;
		var groups = postData.groups;
		//处理当前的postData，往里面添加字段中包含了日期类型的数据
		filters = me.dealFiltersWithDate(filters, list);
		groups = me.dealGroupsWithDate(groups, list);
		//设置到最终的postdata中
		postData.filters = filters;
		postData.groups = groups;
		list.jqGrid ('setGridParam', {
			postData: postData
		});
	}
	this.dealFiltersWithDate=function(filters){
		var resultStr = "";
		if(filters){
			var json = jQuery.parseJSON(filters);
			me.convertRulesWithDate(json.rules, list);
			resultStr = JSON.stringify(json);
		}
		return resultStr;
	}
	

	this.convertRulesWithDate=function (rules){
		$(rules).each(function(){
			var cusType = "";
			var colModels = list.jqGrid('getGridParam','colModel');
			for(var i = 0;i < colModels.length; i++){
				var col = colModels[i];
				//判断是否是日期字段，如果增加参数类型
				if((col.formatter=="date"||col.cusType == "date")
						&& col.index == this.field){
					cusType = "date";
				}else if(col.index == this.field && col.cusType){
					cusType = col.cusType;			
				}
			}
			this.cusType = cusType;
		});
	}

	this.dealGroupsWithDate=function (groups){
		var resultStr = "";
		if(groups){
			var json = jQuery.parseJSON(groups);
			$(json).each(function(){
				me.convertRulesWithDate(this.rules, list);
			});
			resultStr = JSON.stringify(json);
		}
		return resultStr;
	}
}
function FieldtypeAddtionerCreate(){
	this.create = function(list){
		return new FieldtypeAddtioner(list);
	}
}

var FieldtypeAddtionerFactory = new FieldtypeAddtionerCreate();




