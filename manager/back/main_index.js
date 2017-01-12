var fs=require('fs');
var url=require('url');
var getrestapi=require("./getrestapi.js");
var querystring =require("querystring");
var router= require('./router.js');

//默认控制器的地址和端口
var IP="127.0.0.1",
	PORT=8080;

exports.goIndex=function(res,req){
	var readPath= './'+url.parse('../front/index.html').pathname;
	var indexPage=fs.readFileSync(readPath);
	res.end(indexPage);
};
exports.reconnect=function(res,req){
	var readPath= './'+url.parse('../front/reconnect.html').pathname;
	var indexPage=fs.readFileSync(readPath);
	res.end(indexPage);
};

//判断控制器是否可以链接
exports.isconnect = function(res,req){
	var postData ="";
	req.addListener("data",function(postDataChunk){
		postData+=postDataChunk;
	});
	req.addListener("end",function(){
		var ip=querystring.parse(postData).ip;
		var port=querystring.parse(postData).port;
		var options = {
				host: ip,
				port: port,
				path: '/wm/core/health/json',
				method: 'GET',
				headers:{
				}
			};
		console.log(ip+":"+port);
		
		getrestapi.GetRestAPI(null,options,function(Sjson){
			
			if (Sjson==null)
			{
				console.log("fail");
				router.router(res,req,"/reconnect");
			}else{
				console.log("success :"+Sjson);
				router.router(res,req,"/logined");
				IP=ip;
				PORT=port;
			}
			
		});
			
	});
		
};

exports.logined = function(res,req){
	var readPath= './'+url.parse('../front/logined.html').pathname;
	var indexPage=fs.readFileSync(readPath);
	res.end(indexPage);
};


exports.getIP=function(){
	return IP;
};
exports.getPORT=function(){
	return PORT;
};

