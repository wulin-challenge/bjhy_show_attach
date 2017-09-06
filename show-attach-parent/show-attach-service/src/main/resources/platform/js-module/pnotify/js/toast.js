var Toast = {};
/*
 * 自定义Toast组件，集成PNotify
 * API使用：https://sciactive.github.io/pnotify
 */
$(function(){
	PNotify.prototype.options.styling = "bootstrap3";
	PNotify.prototype.options.delay = 2000
	var stack_center = {"dir1": "down", "dir2": "right", "firstpos1": 0, "firstpos2": ($(window).width() / 2) - (Number(PNotify.prototype.options.width.replace(/\D/g, '')) / 2)};
	$(window).resize(function(){
	    stack_center.firstpos2 = ($(window).width() / 2) - (Number(PNotify.prototype.options.width.replace(/\D/g, '')) / 2);
	});
	
	function defaultNotify(opts){
		if(!opts.msg){
			opts.msg = "";
		}
		new PNotify({
		    text: opts.msg,
			stack: stack_center,
			type : opts.type,
		    animate: {
		       	animate: true,
		       	in_class: "slideInDown",
		       	out_class: "slideOutUp"
		   	},
		   	buttons:{
		   		closer:false,
		   		sticker:false
		   	}
		});
	}
	
	Toast.success = function(msg){
		defaultNotify({msg:msg,type:"success"});
	};
	
	Toast.notice = function(msg){
		defaultNotify({msg:msg,type:"notice"});
	};
	
	Toast.info = function(msg){
		defaultNotify({msg:msg,type:"info"});
	};
	
	Toast.error = function(msg){
		defaultNotify({msg:msg,type:"error"});
	};
	
	Toast.pnotify = function(opts){
		return new PNotify(opts);
	}
});




