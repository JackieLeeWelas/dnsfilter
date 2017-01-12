var querystring =require("querystring");
var getrestapi=require("./getrestapi.js");
var MainIndex=require("./main_index.js");


//为客户端提供各种信息。
exports.getinfo= function(res,req,pathname){
	var readPath="";
	console.log(pathname);
	switch(pathname) {
		//将客户端的请求路径转换成Floodlight的REST API路径
		case "/getswinfo":
			readPath='/wm/core/controller/switches/json';
		break;
		case "/gethostinfo":
			readPath='/wm/device/';
		break;
		case "/getflowinfo":
			readPath='/wm/core/switch/all/flow/json';
		break;
		case "/getstaticflowinfo":
			readPath='/wm/staticflowpusher/list/all/json';
		break;
		case "/getdnsfilterrecords":
			readPath='wm/dnsfilter/records'
		break;
		case "/getswlinks":
			readPath='/wm/topology/links/json';
		break;
		case "/getswfilterset":
			readPath='/wm/dnsfilter/swfilterset';
		break;
		case "/getdnsredirectip":
			readPath='/wm/dnsfilter/dnsredirect';
		break;
		case "/gethostfilterset":
		readPath='/wm/dnsfilter/hostfilterset';
		break;
		case "/gethostmacfilterset":
		readPath='/wm/dnsfilter/hostmacfilterset';
		default:
			return;
	}	
		
	var options = {
			host: MainIndex.getIP(),
			port: MainIndex.getPORT(),
			path: readPath,
			method: 'GET',
			headers:{
			}
		};
	
	getrestapi.GetRestAPI(null,options,function(Sjson){
		//定义回调函数，用于回应客户端。
		res.writeHead(200,{"Content-Type":"text/plain","Access-Control-Allow-Origin":"*","Cache-Control":"no-cache"});
		console.log(Sjson+'');
		res.write(Sjson);
		res.end();
	});
};

//增加或删除流表。
exports.staticflow= function(res,req,method){	
	var postData ="";
	//获取客户端POST参数
	req.addListener("data",function(postDataChunk){
		postData+=postDataChunk;
	});
	req.addListener("end",function(){	
			var options = {
				host: MainIndex.getIP(),
				port: MainIndex.getPORT(),
				path: '/wm/staticflowpusher/json',
				method: method,
				headers:{
					'Content-Type': 'application/json',
          		'Content-Length': postData.length
			}
		};
		//定义回调函数，用于回应客户端。
		getrestapi.GetRestAPI(postData,options,function(Sjson){
			res.writeHead(200,{"Content-Type":"text/plain","Access-Control-Allow-Origin":"*","Cache-Control":"no-cache"});
			res.write(Sjson);		
			res.end();
		});				
	});
};


//增加恶意域名到黑名单中
exports.blacklist= function(res,req){
	
	var postData ="";
	req.addListener("data",function(postDataChunk){
		postData+=postDataChunk;
	});
	req.addListener("end",function(){
		
			console.log(postData);
					
			var options = {
				host: MainIndex.getIP(),
				port: MainIndex.getPORT(),
				path: '/wm/dnsfilter/blackdomainname',
				method: 'POST',
				headers:{
					'Content-Type': 'application/json',
          		'Content-Length': postData.length
			}
		};
	
	
		getrestapi.GetRestAPI(postData,options,function(Sjson){
			res.writeHead(200,{"Content-Type":"text/plain","Access-Control-Allow-Origin":"*","Cache-Control":"no-cache"});
			console.log(Sjson+'');
			res.write(Sjson);
			
			res.end();
		});			
	
	});
};

//增加良性域名到白名单中
exports.whitelist= function(res,req){
	
	var postData ="";
	req.addListener("data",function(postDataChunk){
		postData+=postDataChunk;
	});
	req.addListener("end",function(){
		
			console.log(postData);
					
			var options = {
				host: MainIndex.getIP(),
				port: MainIndex.getPORT(),
				path: '/wm/dnsfilter/whitedomainname',
				method: 'POST',
				headers:{
					'Content-Type': 'application/json',
          		'Content-Length': postData.length
			}
		};
	
		getrestapi.GetRestAPI(postData,options,function(Sjson){
			res.writeHead(200,{"Content-Type":"text/plain","Access-Control-Allow-Origin":"*","Cache-Control":"no-cache"});
			console.log(Sjson+'');
			res.write(Sjson);
			
			res.end();
		});			
	
	});
};

//开启或关闭交换机的DNS过滤功能
exports.dnsfilterswitch= function(res,req){
	console.log("blacklist");
	var postData ="";
	req.addListener("data",function(postDataChunk){
		postData+=postDataChunk;
	});
	req.addListener("end",function(){
		
			
			var json=JSON.parse(postData)
			var realPath='/wm/dnsfilter/switch/'+json.swid+'/'+json.action;
			console.log(realPath);
			var options = {
				host: MainIndex.getIP(),
				port: MainIndex.getPORT(),
				path: realPath,
				method: 'PUT',
				headers:{
				'Content-Type': 'text/plain',
				'Content-Length': 0
			}
		};
	
		getrestapi.GetRestAPI(null,options,function(Sjson){
			res.writeHead(200,{"Content-Type":"text/plain","Access-Control-Allow-Origin":"*","Cache-Control":"no-cache"});
			console.log(Sjson+'');
			res.write(Sjson);
			
			res.end();
		});			
	
	});
};

//设在DNS 重定向服务器
exports.setdnsredirect= function(res,req){
	
	var postData ="";
	req.addListener("data",function(postDataChunk){
		postData+=postDataChunk;
	});
	req.addListener("end",function(){
		
			
			var json=JSON.parse(postData)
			var realPath='/wm/dnsfilter/dnsredirect/'+json.dnsredirectip;
			console.log(realPath);
			var options = {
				host: MainIndex.getIP(),
				port: MainIndex.getPORT(),
				path: realPath,
				method: 'PUT',
				headers:{
				'Content-Type': 'text/plain',
          	'Content-Length': 0
			}
		};
	
		getrestapi.GetRestAPI(null,options,function(Sjson){
			res.writeHead(200,{"Content-Type":"text/plain","Access-Control-Allow-Origin":"*","Cache-Control":"no-cache"});
			console.log(Sjson+'');
			res.write(Sjson);		
			res.end();
		});			
	
	});
};

exports.setfilterhost = function(res,req){
	console.log("blacklist");
	var postData ="";
	req.addListener("data",function(postDataChunk){
		postData+=postDataChunk;
	});
	req.addListener("end",function(){
		
			var json=JSON.parse(postData)
			var realPath='/wm/dnsfilter/host/'+json.host_ip+'/'+json.action;
			console.log(realPath);
			var options = {
				host: MainIndex.getIP(),
				port: MainIndex.getPORT(),
				path: realPath,
				method: 'PUT',
				headers:{
				'Content-Type': 'text/plain',
          	'Content-Length': 0			
			}
		};
	
		getrestapi.GetRestAPI(null,options,function(Sjson){
			res.writeHead(200,{"Content-Type":"text/plain","Access-Control-Allow-Origin":"*","Cache-Control":"no-cache"});
			console.log(Sjson+'');
			res.write(Sjson);
			
			res.end();
		});			
	
	});
};

exports.setfilterhostmac = function(res,req){
	console.log("blacklist");
	var postData ="";
	req.addListener("data",function(postDataChunk){
		postData+=postDataChunk;
	});
	req.addListener("end",function(){
		
			var json=JSON.parse(postData)
			var realPath='/wm/dnsfilter/hostmac/'+json.host_mac+'/'+json.action;
			console.log(realPath);
			var options = {
				host: MainIndex.getIP(),
				port: MainIndex.getPORT(),
				path: realPath,
				method: 'PUT',
				headers:{
				'Content-Type': 'text/plain',
          	'Content-Length': 0			
			}
		};
	
		getrestapi.GetRestAPI(null,options,function(Sjson){
			res.writeHead(200,{"Content-Type":"text/plain","Access-Control-Allow-Origin":"*","Cache-Control":"no-cache"});
			console.log(Sjson+'');
			res.write(Sjson);
			
			res.end();
		});			
	
	});
};

//提取特征
exports.feature= function(res,req){

    var postData ="";
    req.addListener("data",function(postDataChunk){
        postData+=postDataChunk;
    });
    req.addListener("end",function(){

        console.log(postData);

        var options = {
            host: MainIndex.getIP(),
            port: MainIndex.getPORT(),
            path: '/wm/classifier/feature',
            method: 'POST',
            headers:{
                'Content-Type': 'application/json',
                'Content-Length': postData.length
            }
        };


        getrestapi.GetRestAPI(postData,options,function(Sjson){
            res.writeHead(200,{"Content-Type":"text/plain","Access-Control-Allow-Origin":"*","Cache-Control":"no-cache"});
            console.log(Sjson+'');
            res.write(Sjson);

            res.end();
        });

    });
};

//训练
exports.train= function(res,req){

    var postData ="";
    req.addListener("data",function(postDataChunk){
        postData+=postDataChunk;
    });
    req.addListener("end",function(){

        console.log(postData);

        var options = {
            host: MainIndex.getIP(),
            port: MainIndex.getPORT(),
            path: '/wm/classifier/train',
            method: 'POST',
            headers:{
                'Content-Type': 'application/json',
                'Content-Length': postData.length
            }
        };


        getrestapi.GetRestAPI(postData,options,function(Sjson){
            res.writeHead(200,{"Content-Type":"text/plain","Access-Control-Allow-Origin":"*","Cache-Control":"no-cache"});
            console.log(Sjson+'');
            res.write(Sjson);

            res.end();
        });

    });
};

exports.test= function(res,req){

    var postData ="";
    req.addListener("data",function(postDataChunk){
        postData+=postDataChunk;
    });
    req.addListener("end",function(){

        console.log(postData);

        var options = {
            host: MainIndex.getIP(),
            port: MainIndex.getPORT(),
            path: '/wm/classifier/test',
            method: 'POST',
            headers:{
                'Content-Type': 'application/json',
                'Content-Length': postData.length
            }
        };


        getrestapi.GetRestAPI(postData,options,function(Sjson){
            res.writeHead(200,{"Content-Type":"text/plain","Access-Control-Allow-Origin":"*","Cache-Control":"no-cache"});
            console.log(Sjson+'');
            res.write(Sjson);

            res.end();
        });

    });
};

exports.predict= function(res,req){

    var postData ="";
    req.addListener("data",function(postDataChunk){
        postData+=postDataChunk;
    });
    req.addListener("end",function(){


        var json=JSON.parse(postData)
        var realPath='/wm/classifier/predict/'+json.domain;
        console.log(realPath);
        var options = {
            host: MainIndex.getIP(),
            port: MainIndex.getPORT(),
            path: realPath,
            method: 'PUT',
            headers:{
                'Content-Type': 'text/plain',
                'Content-Length': 0
            }
        };

        getrestapi.GetRestAPI(null,options,function(Sjson){
            res.writeHead(200,{"Content-Type":"text/plain","Access-Control-Allow-Origin":"*","Cache-Control":"no-cache"});
            console.log(Sjson+'');
            res.write(Sjson);
            res.end();
        });

    });
};




