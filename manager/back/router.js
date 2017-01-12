var fs=require('fs');
var MainIndex=require('./main_index.js');
var getinfo=require('./getinfo.js');
//处理前台的访问请求，根据路径做相应处理
exports.router =function(res,req,pathname){
	switch(pathname){
	case "/IsConnect":
		MainIndex.isconnect(res,req);
	break;
	case "/logined":
		MainIndex.logined(res,req);
	break;
	case "/reconnect":
		MainIndex.reconnect(res,req);
	break;
	case "/":
	case "/index":
		MainIndex.goIndex(res,req);
	break;
	//  统一处理客户端的GET请求
	case "/getswinfo":
	case "/gethostinfo":
	case "/getflowinfo":
	case "/getstaticflowinfo":
	case "/getdnsfilterrecords":
	case "/getswlinks":
	case "/getswfilterset":
	case "/getdnsredirectip":
	case "/gethostfilterset":
		getinfo.getinfo(res,req,pathname);
	break;
	case "/deletestaticflow":
		getinfo.staticflow(res,req,'DELETE');
	break;
	case "/addflow":
		getinfo.staticflow(res,req,'POST');
	break;
	case "/blacklist":
		getinfo.blacklist(res,req);
	break;
	case "/whitelist":
		getinfo.whitelist(res,req);
	break;
	case "/dnsfilterswitch":
		getinfo.dnsfilterswitch(res,req);
	break;
	case "/setfilterhost":
		getinfo.setfilterhost(res,req);
	break;
	case "/setfilterhostmac":
	    getinfo.setfilterhostmac(res,req);
	break;
	case "/setdnsredirectip":
		getinfo.setdnsredirect(res,req);
	break;
	case "/feature":
		getinfo.feature(res,req);
	break;
	case "/train":
		getinfo.train(res,req);
	break;
	case "/test":
		getinfo.test(res,req);
	break;
	case "/predict":
		getinfo.predict(res,req);
    break;

	default:
		dealWithStatic(pathname,res);
	}
};
//处理静态文件的请求
function dealWithStatic(pathname,res){
	var realPath = __dirname+pathname;
	var realPath = "../front"+pathname;
	//console.log("dir:" +__dirname);
	//console.log("path:" +realPath);
	
	fs.exists(realPath, function(exists){
		if(!exists){
	//		console.log( "exist");
			res.writeHead(404,{'Content-Type':'text/plain'});
			res.write(pathname+"canfound");
			res.end();
		}else{
			var pointPosition=pathname.lastIndexOf('.'),
				mineString=pathname.substring(pointPosition+1),
				mineType;
		//	console.log(pathname+" -- "+mineString);
			switch(mineString){
			case 'css':
				mineType="text/css";
				break;
			case 'png':
				mineType="image/png";
				break;
			case 'html':
				mineType="image/png";
				break;
			default:
				mineType="text/plain";
			}
			fs.readFile(realPath,"binary",function(err,file){
				if(err){
					res.writeHead(500,{'Content-Type':'text/plain'});
					res.end(err+"");
				}else{
					res.writeHead(200,{'Content-Type':mineType});
					res.write(file,"binary");
					res.end();
				}
			});
		}
		
	});
	
}
