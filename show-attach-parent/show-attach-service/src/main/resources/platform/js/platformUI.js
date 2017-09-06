
function UIContext(){
	var me = this;
	this.EXPORT_EXCEL_URL = contextPath + "/export/jqgrid"; 
	
	//jqgrid重设宽度(考虑其是否当前浏览器有滚动条)
	this.resizeGridWidth = function(grid, minusWidth){
		var width = window.innerWidth;
	    if (width == null || width < 1){
	        width = $(window).attr('offsetWidth');
	    }
	    // Fudge factor to prevent horizontal scrollbars
	    width = width - minusWidth;
	    if (width > 0 &&
	        Math.abs(width - $(grid).width()) > 5)
	    {
	    	grid.setGridWidth(width);
	    }
	}
	
	//判断是否有横向或者竖向滚动条
	this.hasScrolled = function(el, direction){
		if(direction === "vertical") {
	        return el.scrollHeight > el.clientHeight;
	    }else if(direction === "horizontal") {
	        return el.scrollWidth > el.clientWidth;
	    }
	}
	
	//jqgrid加载完成之后微调矫正其错误的宽度
	this.fineTuneGridSize = function(grid, width){
		if(true){//暂行
			me.resizeGridWidth(grid, width);
		}
	}
	
	//导出excel
	this.exportGrid = function(gridId, sqlTemplate, fileName, extraParams){
		var checkBoxTemplate = "<input role='checkbox' id='cb_list' class='cbox' type='checkbox'/>";
		var colModels = $("#" + gridId).jqGrid('getGridParam','colModel');
		var colNames = $("#" + gridId).jqGrid('getGridParam','colNames');
		var ids = $("#" + gridId).jqGrid ('getGridParam', 'selarrrow');
		var postData = $("#" + gridId).jqGrid ('getGridParam', 'postData');
		var caption = $("#" + gridId).jqGrid ('getGridParam', 'caption');
		var expType = [];
		var fields = [];
		var enabledColNameIndex = [];
		for(var i = 0;i < colModels.length; i++){
			var col = colModels[i];
			if(!col.hidden && col.name != "cb" && col.name.indexOf("customColumn") == -1){
				enabledColNameIndex.push(i);
				fields.push(col.index + "=" + col.name);
				//判断有无导出类型，若有则添加传入后台
				if(col.expType && col.expValue){
					expType.push({field:col.index,type:col.expType,value:col.expValue});
				}
			}
		}
		var filterColNames = [];
		for(var i = 0;i < enabledColNameIndex.length;i++){
			filterColNames.push(colNames[enabledColNameIndex[i]]);
		}
		var fieldsParam = fields.join();
		var colNameParam = filterColNames.join();
		//装载列表头参数，列表字段参数
		var params = {};
		params.exportTypes = JSON.stringify(expType);
		params.colNameParam = colNameParam;
		params.fieldsParam = fieldsParam;
		if(ids.length != 0){
			idsParam = ids.join();
			params.ids = idsParam;
		}
		//装载当前的jqgrid条件参数
		params = $.extend(params, postData);
		//装载hqlTemplaate和文件名称参数
		if(!fileName){
			fileName = caption + ".xls";
		}
		params.fileName = fileName;
		params.sqlTemplate = sqlTemplate;
		//装载格外的参数
		params = $.extend(params, extraParams);
		//构造form表单，进行post提交
		if($("#common_exportForm").get(0)){
			$("#common_exportForm").remove();
		}
		var form = $(document.createElement('form')).attr("id", "common_exportForm")
			.attr('action', this.EXPORT_EXCEL_URL)
				.attr('method','post').css("display", "none");
	    $('body').append(form);
	    for(var attr in params){
	    	var key = attr, value = params[attr];
	    	if(value != "" && !(value instanceof Object)){
			 	$(document.createElement('input')).attr('type', 'hidden').attr('name', key).attr('value', value).appendTo(form);
			}
	    }
	    $(form).submit();
	}
	
	//隐藏表单提交
	this.simulateSubmitForm = function (action, params, type, isMultipart){
		if(!type){
			type = "get";
		}
		var form = $(document.createElement('form')).attr('action', action)
					.attr('method', type).css("display", "none");
		if(isMultipart){
			form.attr("enctype", "multipart/form-data");
		}
	    $('body').append(form);
	    for(var attr in params){
	    	var key = attr, value = params[attr];
	    	if(value != "" && !(value instanceof Object)){
			 	$(document.createElement('input')).attr('type', 'hidden').attr('name', key).attr('value', value).appendTo(form);
			}
	    }
	    $(form).submit();
	    form.remove();
	}
	
	//ajax方法
	this.ajax = function(options){
		var ajaxOptions = $.extend(true, {
			dataType: "json",
			type: "get",
			success: function(data, textStatus,jqXHR){
				if(data.statusCode != null){
					if(data.statusCode == 0){
						if(options.message){
							options.message({message:data.statusText, type:"success"});
						}else{
							if(window.toastr){
								toastr.success(data.statusText);
							}else{
								Toast.success(data.statusText);
							}
						}	
					}else{
						if(options.message){
							options.message({message:data.statusText, type:"error"});
						}else{
							if(window.toastr){
								toastr.error(data.statusText);
							}else{
								Toast.error(data.statusText);
							}
						}
					}
				}
				if(options.afterOperation){
					options.afterOperation(data, textStatus,jqXHR);
				}
			},
			error:function(data, textStatus,jqXHR){
				if(!(data.status == 200)){
					if(options.message){
						options.message({message: "服务器异常", type:"error"});
					}else{
						if(window.toastr){
							toastr.error("服务器异常");
						}else{
							Toast.error("服务器异常");
						}
					}
				}
				if(options.afterOperation){
					options.afterOperation(data, textStatus,jqXHR);
				}
			}
		}, options);
		$.ajax(ajaxOptions);
	}
	
	//列表刷新
	this.refreshGrid = function(grid, options){
		var defaultSortName = grid.jqGrid('getGridParam','sortname');
		var defaultSortOrder = grid.jqGrid('getGridParam','sortorder');
		var defaultOptions = {
			postData: {filters:{}},
			sortname: defaultSortName,
			sortorder: defaultSortOrder,
			page: 1
		};
		var allOptions = $.extend(true, defaultOptions, options);
		grid.jqGrid("setGridParam", allOptions).trigger("reloadGrid");
	}
	
	//填充表单
	this.populateForm = function(formId, data){
		for(var attr in data){
			var formField = $("#" + attr);
			if(!formField[0]){
				formField = $("#" + formId).find("input[name=" + attr + "]");
			}
			if(formField){
				if(formField.attr("type") == "radio"
						|| formField.attr("type") == "checkbox"){
					for(var i = 0;i < formField.length;i++){
						if(data[attr] != null){
							if($(formField.get(i)).attr("value") == data[attr].toString()){
								$(formField.get(i)).prop("checked", true);
							}
						}
					}
				}else if(formField.length == 1 && 
							formField.get(0).nodeName.toLowerCase() == "select"){
					if(data[attr]){
						formField.find("option[value=" + data[attr] + "]").prop('selected', true);
					}else{
						formField.find("option:first").prop("selected", true);
					}
				}else{
					formField.val(data[attr]);
				}
			}
		}
	}
	
	//默认日期控件
	this.defaultJqueryUIDatePick = function(elemId){
		$(elemId).datepicker({dateFormat: "yy-mm-dd"});
	}
	
	//默认yes/no的jqgrid字段显示
	this.defaultYNFormatter = function(cellvalue, options, rowObject){
		if(cellvalue == true){
			return "是";
		}else{
			return "否";
		}
	}
	
	//初始化zTree
	this.initTree = function(inputId, setting, ajaxOption){
		var tree;
		var defaultSetting = {
			data: {
				simpleData: {
					enable: true
				}
			},
			check: {
				enable: false
			}
		};
		$.extend(true, defaultSetting, setting);
		me.ajax({
			url: ajaxOption.url,
			data: ajaxOption.data,
			async: false,
			afterOperation: function(data, textStatus,jqXHR){
				//初始化权限树
				tree = $.fn.zTree.init($("#" + inputId), defaultSetting, data);
			}
		});
		return tree;
	}
	
	//根据父级码值加载码表形成comboTree
	this.loadSystemCodeToComboTreeByParentCode = function(inputId, parentCode, treeOptions){
		return me.initTree(inputId, treeOptions, {
			url: contextPath + "/code/children/code",
			data: {code: parentCode}
		});
	}
	
	//根据父级id加载码表形成comboTree
	this.loadSystemCodeToComboTreeByParentId = function(inputId, parentId, treeOptions){
		return me.initTree(inputId, treeOptions, {
			url: contextPath + "/code/children/" + parentId
		});
	}
	
	//根据父级id加载所有子组织机构
	this.loadOrgToComboTreeByParentId = function(inputId, parentId, treeOptions){
		return me.initTree(inputId, treeOptions, {
			url: contextPath + "/org/children/" + parentId
		});
	}
	
	//根据父级的组织机构类型(码值)加载所有子组织机构
	this.loadOrgToComboTreeByParentType = function(inputId, parentTypeCode, treeOptions){
		return me.initTree(inputId, treeOptions, {
			url: contextPath + "/org/children/code",
			data: {typeCode: parentTypeCode}
		});
	}
	
	//根据组织机构类型加载所有组织机构
	this.loadOrgToComboTreeByType = function(inputId, typeCode, treeOptions){
		return me.initTree(inputId, treeOptions, {
			url: contextPath + "/org/list/code",
			data: {typeCode: typeCode}
		});
	}
	
	//加载所有组织机构
	this.loadAllOrgToComboTree = function(inputId, treeOptions){
		return me.initTree(inputId, treeOptions, {
			url: contextPath + "/org/tree/all"
		});
	}
	
	//通过ztreeAPI对数据进行格式化
	this.formatEasyUITreeData = function(data){
		$(data).each(function(){
			this.text = this.name;
		});
		var utilTreeSetting = {
			data: {
				simpleData: {
					enable: true
				}
			}
		};
		if(!$("#utilTree").get(0)){
			$("body").append("<div style='display:none;'><ul id='utilTree'></ul></div>");
		}
		var utilTree = $.fn.zTree.init($("#utilTree"), utilTreeSetting);
		var nodes = utilTree.transformTozTreeNodes(data);
		return nodes;
	}
	
	//构建工具zTree
	this.buildUtilZtree = function(data){
		var utilTreeSetting = {
			data: {
				simpleData: {
					enable: true
				}
			}
		};
		if(!$("#utilTree2").get(0)){
			$("body").append("<div style='display:none;'><ul id='utilTree2'></ul></div>");
		}
		var utilTree = $.fn.zTree.init($("#utilTree2"), utilTreeSetting, data);
		return utilTree;
	}
	
	//根据行政区划id加载所有的子行政区划数据
	this.loadRegionByParentId = function(parentId, options){
		var result;
		var defaultOption = {
			url: contextPath + "/region/parent/" + parentId,
			async: false,
			afterOperation: function(data){
				result = data;
			}
		};
		$.extend(true, defaultOption, options);
		me.ajax(defaultOption);
		return result;
	}
	
	//加载系统所有人员
	this.loadAllSystemPerson = function(options){
		var result;
		var defaultOption = {
			url: contextPath + "/person/list/all",
			async: false,
			afterOperation: function(data){
				result = data;
			}
		};
		$.extend(true, defaultOption, options);
		me.ajax(defaultOption);
		return result;
	}
	
	//加载系统所有用户
	this.loadAllUser = function(options){
		var result;
		var defaultOption = {
			url: contextPath + "/user/list/all",
			async: false,
			afterOperation: function(data){
				result = data;
			}
		};
		$.extend(true, defaultOption, options);
		me.ajax(defaultOption);
		return result;
	}
	
	//工具类
	this.util = new function(){
		//验证空字符串
		this.strIsNotEmpty = function(str){
			if(str != "" && str != null){
				return true;
			}else{
				return false;
			}
		}
		this.isURL = function(url){
			var protocols = '((https?|s?ftp|irc[6s]?|git|afp|telnet|smb):\\/\\/)+'
			   , userInfo = '([a-z0-9]\\w*(\\:[\\S]+)?\\@)?'
			   , domain = '([a-z0-9]([\\w]*[a-z0-9])*\\.)?[a-z0-9]\\w*\\.[a-z]{2,}(\\.[a-z]{2,})?'
			   , port = '(:\\d{1,5})?'
			   , ip = '\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}'
			   , localhost = '(localhost)'
			   , address = '(\\/\\S*)?'
			   , localhostType = [protocols, localhost, port, address]
			   , domainType = [protocols, userInfo, domain, port, address]
			   , ipType = [protocols, userInfo, ip, port, address]
			   , validate
			validate = function(type){
				return new RegExp('^' + type.join('') + '$', 'i').test(url);
			};
		  	return validate(domainType) || validate(ipType) || validate(localhostType);
		}
	}
	
	//自定义JSONP
	this.JSONP = function(options){
		options.callbackParam = "callback";
		//定义随机数
		var randomStr = String(Math.random(99999999));
		if(randomStr.indexOf(".") != -1){
			randomStr = randomStr.substring(randomStr.indexOf(".") + 1, randomStr.length - 1);
		}
		//动态方法名称
		var callBackMethodName = "platformJsonPCallback_" + randomStr;
		var requestParam = options.callbackParam + "=" + callBackMethodName + "&" + Math.random();
		if(options.param){
			options.param = $.param(options.param);
			requestParam += "&" + options.param;
		}
		var url;
		if(options.url.indexOf("?") == -1){
			url = options.url + "?" + requestParam;
		}else{
			url = options.url + "&" + requestParam;
		}
		//注册全局函数
		window[callBackMethodName] = function (data) {
	    	if(options.complete){
	    		options.complete(data);
	    	}
	    };
		var scriptTemplate = "<script id=\"" + randomStr + "\" type=\"text/javascript\" src=\"" + url + "\"></script>";
		//删除使用过的script标签
		$("head").append(scriptTemplate);
		$("#" + randomStr).remove();
		return callBackMethodName;
	}
	
	//日志
	this.log = function(msg){
		if(window.console){
			console.log(msg);
		}
	};
	
	this.message = function(options){
		if(parent.message){
			parent.message(options);
		}else{
			vue.$message(options);
		}
	}
	
	//map的JS实现
	this.map = function(){
		var map = {
			put: function(key, value){this[key] = value;},
			get : function(key){return this[key];},  
		    contains : function(key){return this.get(key) == null?false:true;},  
		    remove : function(key){delete this[key];}
		};
		return map;
	}
	
	this.arrayMap = function(){
		var map = function(){
			this.elements = new Array();
			//得到map的大小
			this.size = function() {
				return this.elements.length;
			}
			//判断是否为空
			this.isEmpty = function() {
				return (this.elements.length < 1);
			}
			//清空
			this.clear = function() {
				this.elements = new Array();
			}
			//放进map一个对象
			this.put = function(_key, _value) {
				this.elements.push( {
					key : _key,
					value : _value
				});
			}
			//根据键去除一个对象
			this.remove = function(_key) {
				var bln = false;
				try {
					for (i = 0; i < this.elements.length; i++) {
						if (this.elements[i].key == _key && typeof this.elements[i].key == typeof _key) {
							this.elements.splice(i, 1);
							return true;
						}
					}
				} catch (e) {
					bln = false;
				}
				return bln;
			}
			//根据键得到一个对象
			this.get = function(_key) {
				try {
					for (i = 0; i < this.elements.length; i++) {
						if (this.elements[i].key == _key && typeof this.elements[i].key == typeof _key) {
							return this.elements[i].value;
						}
					}
				} catch (e) {
					return null;
				}
			}
			//返回指定索引的一个对象
			this.element = function(_index) {
				if (_index < 0 || _index >= this.elements.length) {
					return null;
				}
				return this.elements[_index];
			}
			//是否包含键
			this.containsKey = function(_key) {
				var bln = false;
				try {
					for (i = 0; i < this.elements.length; i++) {
						if (this.elements[i].key == _key && typeof this.elements[i].key == typeof _key) {
							bln = true;
						}
					}
				} catch (e) {
					bln = false;
				}
				return bln;
			}
			//是否包含值
			this.containsValue = function(_value) {
				var bln = false;
				try {
					for (i = 0; i < this.elements.length; i++) {
						if (this.elements[i].value == _value && typeof this.elements[i].value == typeof _value) {
							bln = true;
						}
					}
				} catch (e) {
					bln = false;
				}
				return bln;
			}
			//得到所有的值
			this.values = function() {
				var arr = new Array();
				for (i = 0; i < this.elements.length; i++) {
					arr.push(this.elements[i].value);
				}
				return arr;
			}
			//得到所有的键
			this.keys = function() {
				var arr = new Array();
				for (i = 0; i < this.elements.length; i++) {
					arr.push(this.elements[i].key);
				}
				return arr;
			}
		}
		return new map();
	}
	
	
}


//系统JS库
var PlatformUI = new UIContext();

