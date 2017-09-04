/**
 * option参数
 * 	data:需要传输表单之外的参数json格式
 * 	callback:提交表单之后的回调函数
 * 	check:表单的验证函数
 * 	
 */

(function ($) {
    $.fn.asyncSubmit = function (option) {
    	var me = this;
    	//防止多次提交检测
    	var firstCheck =true;
    	//设置验证
    	if(me.get(0).nodeName.toLowerCase() == "form"){
    		me.submit(function(e){
     		    //拦截上传form，可做验证
    			var checkResult = true;
    			if(firstCheck){
    				if(option.check){
        				checkResult = option.check();
        			}
    				firstCheck = false;
    			}
    			
    			return checkResult;
     		});
    	}
    	//构建内部iframe隐藏提交
 		var dom = document.createElement("iframe");
 		var random = "i_" +　Math.random() * 1000000;
 		$(dom).attr("name", random).css("display", "none");
 		$("body").append(dom);
 		//清空额外参数
 		var extraParamDiv = "extraParamDiv";
 		$("#" +　extraParamDiv).remove();
 		//设置额外参数
 		var extraDataArray = new Array();
 		if(option.data){
 			if(option.data instanceof Object){
 				var tempDiv = document.createElement("div");
				$(tempDiv).attr("id", extraParamDiv);
				$(tempDiv).css({"display":"none"});
 				for(var attr in option.data){
 					var inputEle = document.createElement("input");
 					$(inputEle).attr("name", attr);
 					$(inputEle).val(option.data[attr]);
 					extraDataArray.push(inputEle);
 					$(tempDiv).append(inputEle);
 				}
 				me.append(tempDiv);
 			}
 		}
 		me.attr("target", random);
 		if(window["submitCallback"] == null){
 			window["submitCallback"] = function(data){
 				//提交完后清除隐藏iframe
 				$(dom).remove()
 				//设置回调
 		 		if(option.callback){
 		 			option.callback(data);
 		 		}
 			}
 		}
 		me.submit();
    };
})(jQuery);