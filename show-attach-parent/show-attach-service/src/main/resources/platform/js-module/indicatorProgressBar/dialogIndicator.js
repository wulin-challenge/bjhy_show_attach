(function ($) {
    $.fn.extend({
    	dialogIndicator:function(option){
			var me = this;
    		var dialogIndicator = new DialogIndicator(me,option);
			dialogIndicator.entry();
    		return dialogIndicator;
    	}
    });
})($);

var DialogIndicator = function(currentElement,option){
	var me = this;
	
	//充满整个背景的div
	var fullBackGroundDiv = null;
	
	//圆形进度的div
	var indicatorProcessDiv = null;
	
	//径向指示器对象(进度条对象)
	var radialIndicator = null;
	
	// 进度条的数值
	var progressNumber = 0;
	
	me.entry = function(){
		//初始化参数
		init();
		//合并option参数
		extendOptionParams();
		//渲染html
		renderHtml();
		//渲染css样式
		renderStyle();
		//显示dialog窗体
		showDialog();
		//显示径向指示器(进度条)
		showRadialIndicator();
		//alert(12321);
		//隐藏dialog窗体
		//destroyDialog();
	}
	
	/**
	 * 初始化参数
	 */
	var init = function(){
		fullBackGroundDiv = "full_back_ground"+getRandomNumber();
		indicatorProcessDiv = "indicator_process_div"+getRandomNumber();
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
	 * 默认的参数
	 */
	var defaultParams = function(){
		var defaultOptions = {
			attachmentElement:null,//绑定的额外元素,这里主要是指绑定的附件元素
			dialogPrompt:"处理中,请稍后..." //dialogPrompt : 弹出框的提示文字
		};
		return defaultOptions;
	}
	
	/**
	 *渲染html
	 */
	var renderHtml = function(){
		var dialogBeforeHtml = "<div id='"+fullBackGroundDiv+"'></div>";
		$(currentElement).before(dialogBeforeHtml);
		
		var dialogAppendHtml = option.dialogPrompt+"<div id='"+indicatorProcessDiv+"'></div>";
		$(currentElement).append(dialogAppendHtml);
		
	}
	
	/**
	 * 渲染css样式
	 */
	var renderStyle = function(){
		$("#"+fullBackGroundDiv).css({"background-color":"gray","left":"0","opacity":"0.5","position":"absolute","top":"0","z-index":"10000","filter":"alpha(opacity=80)","-moz-opacity":"0.5","-khtml-opacity":"0.5"});
		
		$("#"+indicatorProcessDiv).css({"position":"relative","left":"145px","top":"5px"});
		
//		$(currentElement).css({"background-color":"#fff","border":"1px solid rgba(0,0,0, 0.8)","height":"150px","left":"50%","margin":"-200px 0 0 -200px","padding":"1px","position":"fixed !important","position":"absolute","top":"90%","width":"400px","z-index":"10001","border-radius":"5px","display":"none"});
	}
	
	/**
	 * 显示dialog窗体
	 */
	var showDialog = function(){
		var backGroundHeight = "200%";
		var backGroundWidth = "100%";
		//网页被卷去的高
		var scrollTop = document.body.scrollTop;
		//浏览器可见区域高度
		var viewHeight = document.documentElement.clientHeight;
		var top = (viewHeight/2)+scrollTop; //获取当前屏幕的正中间位置
		$(currentElement).css({"background-color":"#fff",
			"border":"1px solid rgba(0,0,0, 0.8)",
			"height":"150px",
			"left":"50%",
			"margin":"-200px 0 0 -200px",
			"padding":"1px",
			"position":"fixed !important",
			"position":"absolute","top":top+"px",
			"width":"400px","z-index":"10001",
			"border-radius":"5px",
			"display":"none"});
		
		$("#"+fullBackGroundDiv).css({   
			height:backGroundHeight,   
			width:backGroundWidth,   
			display:"block"   
		}); 
		$(currentElement).show();  
	}
	
	/**
	 * 隐藏dialog窗体,并销毁中间模态层
	 */
	me.destroyDialog = function(){
		$(currentElement).hide();
		$(currentElement).empty();
		$("#"+fullBackGroundDiv).hide();
		$("#"+fullBackGroundDiv).remove();
		$("#"+indicatorProcessDiv).remove();
	}
	
	/**
	 * 显示径向指示器(进度条)
	 */
	var showRadialIndicator= function(){
		radialIndicator = $("#"+indicatorProcessDiv).radialIndicator({
					 barColor: {
						0: '#FF0000',
						33: '#FFFF00',
						66: '#0066FF',
						100: '#33CC33'
					},
					percentage: true}).data('radialIndicator');
	}
	
	/**
	 * 显示径向指示器(进度条)值
	 */
	me.setRadialIndicatorValue = function(val){
		if(radialIndicator == null){
			alert("径向指示器(进度条)对象为空");
			return;
		}
		
		radialIndicator.value(val);
	}
	
	/**
	 * 进度条间隔:该函数描述进度条的执行情况
	 */
	me.progressInterval = function(progressParam){
		progressParam = $.extend(defaultProgressParam(), progressParam);
		
		var interval = setInterval(function(){
			me.setRadialIndicatorValue(progressNumber);
			if(progressNumber>100){
				 window.clearInterval(interval);
				 me.destroyDialog();
			}
			if(progressParam.waitingInterval == progressNumber ||(progressNumber>=progressParam.waitingInterval && progressNumber<100)){
				//等待不做任何事情
			}else{
				progressNumber += 1;
			}
		},progressParam.timeInterval);
	}
	
	/**
	 * 得到进度条数值
	 */
	me.getProgressNumber = function(){
		return progressNumber;
	}
	
	/**
	 * 设置进度条数值
	 */
	me.setProgressNumber = function(progressNumber2){
		progressNumber = progressNumber2;
	}
	
	/**
	 * 默认进度条参数
	 */
	var defaultProgressParam = function(progressParam){
		var defaultParam = {
				timeInterval:500, //时间间隔
				waitingInterval:98 //当进度数值增加到98时就等待
		};
		return defaultParam;
	}
	
	/**
	 * 得到一个随机数
	 */
	var getRandomNumber = function(){
		 var randomNumber = Math.floor(Math.random()*100000+1);
		 return randomNumber;
	}
	
	
}