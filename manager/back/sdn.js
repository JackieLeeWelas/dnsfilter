var http=require('http');

var url=require('url');
var router= require('./router.js');
//创建服务器
var SDNServer=http.createServer(function(req,res){
	var pathname=url.parse(req.url).pathname;
	req.setEncoding("utf8");
	res.writeHead(200,{'Content-Type':'text/html'});
	router.router(res,req,pathname);
});
//设置服务器地址和端口
SDNServer.listen(3000,"127.0.0.1");
console.log("Server running at http://127.0.0.1:3000");

//SNDServer.listen(3000,"192.168.11.121");
//console.log("Server running at http://192.168.11.121:3000");
