var http = require('http');
var equal = require('assert').equal;

// 调用floodlight的RestAPI
exports.GetRestAPI = function(jsondata,options,callback){
		//使用node.js的request对象	
		var req = http.request(options, function (res) {
			//equal(200, res.statusCode);
			var buffer="";
			//接收数据
        	res.on('data', function(chunk) {
					buffer+=chunk;
        	});
        	res.on('end', function() {
        			//调用回调函数
        			callback(buffer);
        	});					
		});
		if (jsondata!=null) {
			req.write(jsondata);
		}		
		req.on('error', function(e) {
			console.log('problem with request: ' + e.message);
			callback(null);
		});
		req.end();	
};

exports.GetRestAPI2 = function(jsondata,options,callback){
		//使用node.js的request对象	
		var req = http.request(options, function (res) {
			equal(200, res.statusCode);
			var buffer="";
			//接收数据
        	res.on('data', function(chunk) {
					buffer+=chunk;
        	});
        	res.on('end', function() {
        			//调用回调函数
        			callback(buffer);
        	});					
		});
		if (jsondata!=null) {
			req.write(jsondata);
		}		
		req.on('error', function(e) {
			console.log('problem with request: ' + e.message);
			callback(null);
		});
		req.end();	
};

exports.GetRestAPI1 = function(jsondata,options,callback){
		
		
		var req = http.request(options, function (res) {
			equal(200, res.statusCode);
			var chunks = [], length = 0;
        	res.on('data', function(chunk) {
        		length += chunk.length;
        		chunks.push(chunk);
        		});
        	res.on('end', function() {
        		var buffer = new Buffer(length);
        		// delay copy
        		for(var i = 0, pos = 0, size = chunks.length; i < size; i++) {
        			chunks[i].copy(buffer, pos);
        			pos += chunks[i].length;
        			}
        			callback(buffer);
        			});					
		});
		if (jsondata!=null) {
			req.write(jsondata)
		}
		
		req.on('error', function(e) {
			console.log('problem with request: ' + e.message);
			callback(null);
		});
		req.end();
		
};



