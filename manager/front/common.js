function clearRow(){ 
	objTable= document.getElementById("testTbl"); 
	var length= objTable.rows.length ;
	for( var i=length-1; i>=0; i-- )
	{
		objTable.deleteRow(i);
	//	alert(objTable.rows.length);
	}
	
}
function getXMLHttpRequest(){
	try{
		try{
			return new ActiveXObject("Microsoft.XMLHTTP");	
		}catch (e) {
			return new ActiveXObject("Msxml2.XMLHTTP");		
		}
	}catch (e) {
		return new XMLHttpRequest();
	}
}
//从后台获取switch信息，并在switch页面中显示
function GetSW(){
	resetcontent();
	var xhr=getXMLHttpRequest();
	xhr.open("GET","/getswinfo",true);
	xhr.onreadystatechange=function(){
		if(xhr.readyState==4){
			if(xhr.status==200){
				document.getElementById("headertitle").innerHTML="SDN: 交换机状态";
				var MyObject = JSON.parse(xhr.responseText);
				clearRow();
				var newTr = testTbl.insertRow(testTbl.rows.length); 
				var newTd0 = newTr.insertCell();
				newTd0.innerHTML="DPID";
				var newTd1 = newTr.insertCell(); 
				newTd1.innerHTML="IP Address";
				var newTd2 = newTr.insertCell();
				newTd2.innerHTML="Connect Since";

				var Count=false;
				for(var i=0;i<MyObject.length;i++){
					Count=!Count;
					var newTr = testTbl.insertRow(testTbl.rows.length); 
					var newTd0 = newTr.insertCell();
					newTd0.innerHTML=MyObject[i].switchDPID;
					var newTd1 = newTr.insertCell(); 
					newTd1.innerHTML=MyObject[i].inetAddress;
					var newTd2 = newTr.insertCell();
					var d=new Date(MyObject[i].connectedSince);					
					newTd2.innerHTML=d.toLocaleString();
					if(Count){newTr.style.background="#FFE1FF";} 
					else {newTr.style.background="#FFEFD5";} 
					
				}
						
				}
				
			}
		};
		xhr.send(null);
}
//从后台获取host信息，并在host页面中显示
function GetHost(){
	resetcontent();
	var xhr=getXMLHttpRequest();
	xhr.open("GET","/gethostinfo",true);
	xhr.onreadystatechange=function(){
		if(xhr.readyState==4){
			if(xhr.status==200){
				document.getElementById("headertitle").innerHTML="SDN: 主机状态";		
				var MyObject = JSON.parse(xhr.responseText);
				clearRow();
				if(MyObject.length==0)
					return;
				
				var newTr = testTbl.insertRow(testTbl.rows.length); 
				var newTd0 = newTr.insertCell();
				newTd0.innerHTML="IP";
				var newTd1 = newTr.insertCell(); 
				newTd1.innerHTML="MAC";
				var newTd2 = newTr.insertCell();
				newTd2.innerHTML="AttachmentPoint";
				var newTd3 = newTr.insertCell();
				newTd3.innerHTML="Last Seen";		
				
				var Count=false;
				for(var i=0;i<MyObject.length;i++){
					if(MyObject[i].ipv4.length==0)continue;	
					Count=!Count;
					var newTr = testTbl.insertRow(testTbl.rows.length); 
					var newTd0 = newTr.insertCell();
					if(MyObject[i].ipv4.length>1&&MyObject[i].ipv4[0]=="0.0.0.0")
						newTd0.innerHTML=MyObject[i].ipv4[1];
					else 
						newTd0.innerHTML=MyObject[i].ipv4;
					var newTd1 = newTr.insertCell(); 
					newTd1.innerHTML=MyObject[i].mac;
					var newTd2 = newTr.insertCell();
					if(MyObject[i].attachmentPoint.length!=0)
						newTd2.innerHTML=MyObject[i].attachmentPoint[0].switchDPID+"-"+MyObject[i].attachmentPoint[0].port;

					var newTd3 = newTr.insertCell();	
					var d=new Date(MyObject[i].lastSeen);					
					newTd3.innerHTML=d.toLocaleString();
					if(Count){newTr.style.background="#FFE1FF";} 
					else {newTr.style.background="#FFEFD5";} 		
				}
			
				}
			}
		};
		xhr.send(null);
}
//从后台获取流表，并在flow页面中显示
function GetFlow(){
	resetcontent();
	var xhr=getXMLHttpRequest();
	xhr.open("GET","/getflowinfo",true);
	xhr.onreadystatechange=function(){
		if(xhr.readyState==4){
			if(xhr.status==200){
				document.getElementById("headertitle").innerHTML="SDN: 流表";

				var MyObject = JSON.parse(xhr.responseText); 							
				clearRow();
							
				for(var i in MyObject){
					
					if(MyObject[i].flows.length==0){
						continue; 
					}					
					
					var newTr = testTbl.insertRow(testTbl.rows.length); 
					var newTd = newTr.insertCell();
					newTd.innerHTML="<h2>"+i+"</h2>";
					
					var newTr = testTbl.insertRow(testTbl.rows.length); 
					var newTd0 = newTr.insertCell();
					newTd0.innerHTML="cookie";
					var newTd1 = newTr.insertCell(); 
					newTd1.innerHTML="match";
					var newTd2 = newTr.insertCell();
					newTd2.innerHTML="actions";
					var newTd3 = newTr.insertCell();
					newTd3.innerHTML="byteCount";	
					var newTd4 = newTr.insertCell();
					newTd4.innerHTML="packetCount";	
					var newTd5 = newTr.insertCell();
					newTd5.innerHTML="duration";	
					var newTd6 = newTr.insertCell();
					newTd6.innerHTML="hardTimeout";	
					var newTd7 = newTr.insertCell();
					newTd7.innerHTML="idleTimeout";
			
					var Count=false;
					for(var j=0;j<MyObject[i].flows.length;j++)
					{
						Count=!Count;
						var newTr = testTbl.insertRow(testTbl.rows.length); 
						var newTd0 = newTr.insertCell();
						newTd0.innerHTML=MyObject[i].flows[j].cookie;
						var newTd1 = newTr.insertCell(); 
						for(var k in MyObject[i].flows[j].match)
							newTd1.innerHTML+=k+" : "+MyObject[i].flows[j].match[k]+"</br>"
						
						//newTd1.innerHTML=JSON.stringify(MyObject[i].flows[j].match);
						var newTd2 = newTr.insertCell();
						for(var k in MyObject[i].flows[j].actions)
							newTd2.innerHTML+=k+" : "+MyObject[i].flows[j].actions[k]+"</br>"
						//newTd2.innerHTML=MyObject[i].flows[j].actions;
						
						var newTd3 = newTr.insertCell();
						newTd3.innerHTML=MyObject[i].flows[j].byteCount;	
						var newTd4 = newTr.insertCell();
						newTd4.innerHTML=MyObject[i].flows[j].packetCount;	
						var newTd5 = newTr.insertCell();
						newTd5.innerHTML=MyObject[i].flows[j].durationSeconds;	
						var newTd6 = newTr.insertCell();
						newTd6.innerHTML=MyObject[i].flows[j].hardTimeoutSec;	
						var newTd7 = newTr.insertCell();
						newTd7.innerHTML=MyObject[i].flows[j].idleTimeoutSec;
						if(Count){newTr.style.background="#FFE1FF";} 
						else {newTr.style.background="#FFEFD5";} 				

					}
	
					
					}
			
				}
			}
		};
		xhr.send(null);
}




//初始化流表管理流表页面。
function ManageFlow(){
		
	resetcontent();
	
	var xhr=getXMLHttpRequest();
	xhr.open("GET","/getstaticflowinfo",true);
	xhr.onreadystatechange=function(){
		if(xhr.readyState==4){
			if(xhr.status==200){
				document.getElementById("headertitle").innerHTML="SDN: 管理流表";
				getaddflowform();
			
				
				var MyObject = JSON.parse(xhr.responseText); 			
			 
				clearRow();
					
				for(var i in MyObject){
					
					if(MyObject[i].length==0){
						continue; 
					}					
					
					var newTr = testTbl.insertRow(testTbl.rows.length); 
					var newTd = newTr.insertCell();
					newTd.innerHTML="<h2>"+i+"</h2>";
					var newTr = testTbl.insertRow(testTbl.rows.length); 
					var newTd0 = newTr.insertCell();
					newTd0.innerHTML="Flow Name";
					var newTd1 = newTr.insertCell();
					newTd1.innerHTML="cookie";
					var newTd2 = newTr.insertCell();
					newTd2.innerHTML="match";
					var newTd3 = newTr.insertCell();
					newTd3.innerHTML="actions";
					var newTd4 = newTr.insertCell();
					newTd4.innerHTML="hardTimeout";
					var newTd4 = newTr.insertCell();
					newTd4.innerHTML="idleTimeout";						
					
					var Count=false;
					for(var j =0; j<MyObject[i].length;j++)
					{
						Count=!Count;
						//alert("test");
						for (var k in MyObject[i][j] ){
							var newTr = testTbl.insertRow(testTbl.rows.length); 
							var newTd0 = newTr.insertCell();
							newTd0.innerHTML=k;
							var newTd1 = newTr.insertCell();
							newTd1.innerHTML=MyObject[i][j][k].cookie;
							var newTd2 = newTr.insertCell();
							for(var l in MyObject[i][j][k].match)
							newTd2.innerHTML+=l+" : "+MyObject[i][j][k].match[l]+"</br>"
							//newTd2.innerHTML=MyObject[i][j][k].match;
							var newTd3 = newTr.insertCell();
							for(var l in MyObject[i][j][k].actions)
							newTd3.innerHTML+=l+" : "+MyObject[i][j][k].actions[l]+"</br>"
							//newTd3.innerHTML=MyObject[i][j][k].actions;
							var newTd4 = newTr.insertCell();
							newTd4.innerHTML=MyObject[i][j][k].hardTimeoutSec;
							var newTd4 = newTr.insertCell();
							newTd4.innerHTML=MyObject[i][j][k].idleTimeoutSec;	
							var newTd4 = newTr.insertCell();
							newTd4.innerHTML='<input type="button" value="Delete" name="'+k+'" onclick=DeleteStaticFlow(name) />';
							
							
						}
						if(Count){newTr.style.background="#FFE1FF";} 
						else {newTr.style.background="#FFEFD5";} 	
					}			
				}					
				}
			}
		};
		xhr.send(null);
}
//删除被选择流表
function DeleteStaticFlow(flowname){
	
	var xhr=getXMLHttpRequest();
	xhr.open("POST","/deletestaticflow",true);
	xhr.onreadystatechange=function(){
		if(xhr.readyState==4){
			if(xhr.status==200){				
					alert("delete success");
					getaddflowform();
					ManageFlow();
					//alert("test");
				}
			}
		};		
		var json=new Object();
		json.name=flowname;
		xhr.send(JSON.stringify(json));
}

//从后台获取控制器关联所有swtich的ID，并填充addflow中DPID的选择控件。
function FillSWID(){
	var xhr=getXMLHttpRequest();
	xhr.open("GET","/getswinfo",true);
	xhr.onreadystatechange=function(){
		if(xhr.readyState==4){
			if(xhr.status==200){
				var MyObject = JSON.parse(xhr.responseText);
				
				for(var i=0;i<MyObject.length;i++){

					var selObj = document.getElementById("selsw");
		            var Option = document.createElement("OPTION");
      		        Option.value = MyObject[i].switchDPID;
            		Option.text = MyObject[i].switchDPID;
           			selObj.options.add(Option);								
				    }				
				}
			}
		};
		xhr.send(null);
}

//初始化addflow的表单
function getaddflowform() {
	var xhr=getXMLHttpRequest();
	xhr.open("GET","/addflow.html",true);
	xhr.onreadystatechange=function(){
		if(xhr.readyState==4){
			if(xhr.status==200){				
					//alert(xhr.responseText);
					//ManageFlow();
					//alert("test");
					
					document.getElementById("formdiv").innerHTML=xhr.responseText;
					FillSWID();
				}
			}
		};
		xhr.send();

}

//处理addflow提交的表单，检查各个参数是否符合要求，并转换成json后提交后台。
function SubFlowForm(){
		//alert();
		var json =new Object();
		json.switch=document.flowform.selsw.value;
		
		if(document.flowform.flowname.value=="")
		{
			alert("FlowName cannot be empty");
			
			return;
		}
		json.name=document.flowform.flowname.value;
		
		if(document.getElementById("ip_base").checked){
			if(document.flowform.src_ip.value!='')
				{
					json.ipv4_src=document.flowform.src_ip.value;
					json.eth_type = 0x0800;
				}
			if(document.flowform.dst_ip.value!='')
				{
					json.ipv4_dst=document.flowform.dst_ip.value;
					json.eth_type = 0x0800;
				}
		}
		if(document.getElementById("mac_base").checked){
			if(document.flowform.src_mac.value!='')
				{
					json.eth_src=document.flowform.src_mac.value;					
				}
			if(document.flowform.dst_mac.value!='')
				{
					json.eth_dst=document.flowform.dst_mac.value;
				}
		}
		
		if(document.getElementById("port_base").checked){
			if(document.flowform.inport.value!='')
			{
				if(document.flowform.inport.value<0)
				{
					alert("The inport must greater than or equal to 0!");
					return;
				}
				json.in_port=document.flowform.inport.value;					
			}
		}
		
		if(document.getElementById("protocol_base").checked){
			if(document.getElementById("ICMP").checked)
			{
				json.eth_type = 0x0800;
				json.ip_proto=0x01;					
			}else if(document.getElementById("TCP").checked)
			{
				json.eth_type = 0x0800;
				json.ip_proto=0x06;
				
				if(document.flowform.tcp_src.value!='')
				{
					if(document.flowform.tcp_src.value<1)
					{
						alert("The tcp_src must greater than 0!");
						return;
					}
					json.tcp_src=document.flowform.tcp_src.value;					
				}				
				if(document.flowform.tcp_dst.value!='')
				{
					if(document.flowform.tcp_dst.value<1)
					{
						alert("The tcp_dst must greater than 0!");
						return;
					}
					json.tcp_dst=document.flowform.tcp_dst.value;					
				}
				
			}else if(document.getElementById("UDP").checked){
				json.eth_type = 0x0800;
				json.ip_proto=0x11;
				if(document.flowform.udp_src.value!='')
				{
					if(document.flowform.udp_src.value<1)
					{
						alert("The udp_src must greater than 0!");
						return;
					}
					json.udp_src=document.flowform.udp_src.value;					
				}				
				if(document.flowform.udp_dst.value!='')
				{
					if(document.flowform.udp_dst.value<1)
					{
						alert("The udp_dst must greater than 0!");
						return;
					}
					json.tcp_dst=document.flowform.udp_dst.value;					
				}
			}
		}
		
		if(document.flowform.idletimeout.value!='')
		{
			if(document.flowform.idletimeout.value<0)
			{
				alert("The idletimeout must greater than or equal  to 0!");
				return;
			}
			json.idle_timeout=document.flowform.idletimeout.value;					
		}
		if(document.flowform.hardtimeout.value!='')
		{
			if(document.flowform.hardtimeout.value<0)
			{
				alert("The hardtimeout must greater than or equal to 0!");
				return;
			}
			json.hard_timeout=document.flowform.hardtimeout.value;					
		}
		
		if(document.getElementById("flood").checked){
			json.actions="output=flood"
		}else if(document.getElementById("out_port").checked) {
			if (document.flowform.out_port_n.value=="") {
				alert("out_port cannot be empty");
				return;
			}
			if (document.flowform.out_port_n.value<0) {
				alert("The inport must greater than or equal to 0!");
				return;
			}
			json.actions="output="+document.flowform.out_port_n.value;
		}else {
			json.actions="drop";
		}
		
		var xhr=getXMLHttpRequest();
		xhr.open("POST","/addflow",true);
		xhr.onreadystatechange=function(){
		if(xhr.readyState==4){
			if(xhr.status==200){				
					ManageFlow();
					alert(xhr.responseText);				
				}
			}
		};
		xhr.send(JSON.stringify(json));
				
}



function ActiveInput(boxid,input1,input2){
	

	if(document.getElementById(boxid).checked)
	{    
		//alert("checkbox is checked");
		document.getElementById(input1).disabled=false;
		document.getElementById(input2).disabled=false;
		
	}else{
		//alert("checkbox is not checked");
		document.getElementById(input1).disabled=true;
		document.getElementById(input2).disabled=true;
	}
	
}

function ActiveRatio(boxid,ratio1,ratio2,ratio3){
	

	if(document.getElementById(boxid).checked)
	{    
		//alert("checkbox is checked");
		document.getElementById(ratio1).disabled=false;
		document.getElementById(ratio2).disabled=false;
		document.getElementById(ratio3).disabled=false;
		ActivePortInput();
	}else{
		//alert("checkbox is not checked");
		document.getElementById(ratio1).disabled=true;
		document.getElementById(ratio2).disabled=true;
		document.getElementById(ratio3).disabled=true;
		document.getElementById("udp_src").disabled=true;
		document.getElementById("udp_dst").disabled=true;
		document.getElementById("tcp_src").disabled=true;
		document.getElementById("tcp_dst").disabled=true;
	}
	
}

function ActivePortInput(){
	

	if(document.getElementById("ICMP").checked)
	{    
		//alert("checkbox is checked");
		document.getElementById("udp_src").disabled=true;
		document.getElementById("udp_dst").disabled=true;
		document.getElementById("tcp_src").disabled=true;
		document.getElementById("tcp_dst").disabled=true;
	}else if(document.getElementById("TCP").checked) {
		//alert("checkbox is not checked");
		document.getElementById("udp_src").disabled=true;
		document.getElementById("udp_dst").disabled=true;
		document.getElementById("tcp_src").disabled=false;
		document.getElementById("tcp_dst").disabled=false;
	}else if(document.getElementById("UDP").checked) {
		//alert("checkbox is not checked");
		document.getElementById("udp_src").disabled=false;
		document.getElementById("udp_dst").disabled=false;
		document.getElementById("tcp_src").disabled=true;
		document.getElementById("tcp_dst").disabled=true;
	}
	
}

//重置页面
function resetcontent(){
	var xhr=getXMLHttpRequest();
	xhr.open("GET","/content.html",true);
	xhr.onreadystatechange=function(){
		if(xhr.readyState==4){
			if(xhr.status==200){				
					//alert(xhr.responseText);
					//ManageFlow();
					//alert("test");
					
					document.getElementById("content").innerHTML=xhr.responseText;

				}
			}
		};
		xhr.send();
}


//从后台获取DNSFilter的过滤结果
function getDNSRecords(){
	resetcontent();
	var xhr=getXMLHttpRequest();
	xhr.open("GET","/getdnsfilterrecords",true);
	xhr.onreadystatechange=function(){
		if(xhr.readyState==4){
			if(xhr.status==200){
				document.getElementById("headertitle").innerHTML="SDN: DNS域名过滤记录";
				
				var MyObject = JSON.parse(xhr.responseText); 							
				clearRow();
																			
				var newTr = testTbl.insertRow(testTbl.rows.length); 
				var newTd0 = newTr.insertCell();
				newTd0.innerHTML="Time";
				var newTd1 = newTr.insertCell(); 
				newTd1.innerHTML="Host";
				var newTd2 = newTr.insertCell();
				newTd2.innerHTML="DNS Server";
				var newTd3 = newTr.insertCell();
				newTd3.innerHTML="SWID";	
				var newTd4 = newTr.insertCell();
				newTd4.innerHTML="Query Name";	
					
			
				var Count=false;
				for(var i=MyObject.length-1;i>=0;i--)
				{
					Count=!Count;
					var newTr = testTbl.insertRow(testTbl.rows.length); 
					var newTd0 = newTr.insertCell();
					newTd0.innerHTML=MyObject[i].time;
					var newTd1 = newTr.insertCell(); 
					newTd1.innerHTML=MyObject[i].host;
					var newTd2 = newTr.insertCell();
					newTd2.innerHTML=MyObject[i].dnsserver;
					var newTd3 = newTr.insertCell();
					newTd3.innerHTML=MyObject[i].swid;	
					var newTd4 = newTr.insertCell();
					newTd4.innerHTML=MyObject[i].queryname;
					if(Count){newTr.style.background="#FFE1FF";} 
					else {newTr.style.background="#FFEFD5";} 				

				
				
				}
				}
			}
		};
		xhr.send(null);
}

//初始化dnsfilter管理页面
function ManageDNSFilter(){
	
	resetcontent();
	var xhr=getXMLHttpRequest();
	xhr.open("GET","/dnsfilter.html",true);
	xhr.onreadystatechange=function(){
		if(xhr.readyState==4){
			if(xhr.status==200){
					document.getElementById("headertitle").innerHTML="SDN: 恶意域名防护管理";
					//alert(xhr.responseText);
					//ManageFlow();
					//alert("test");
					
					document.getElementById("formdiv").innerHTML=xhr.responseText;
					FillEnableList();
					getDNSRedirectIP();
					FillFilterHostList();
					FillFilterHostMacList();
				}
			}
		};
		xhr.send();
	
}

function Classifier() {
    resetcontent();
    var xhr=getXMLHttpRequest();
    xhr.open("GET","/classifier.html",true);
    xhr.onreadystatechange=function(){
        if(xhr.readyState==4){
            if(xhr.status==200){
                document.getElementById("headertitle").innerHTML="SDN: 域名分类器";
                //alert(xhr.responseText);
                //ManageFlow();
                //alert("test");

                document.getElementById("formdiv").innerHTML=xhr.responseText;
                // FillEnableList();
                // getDNSRedirectIP();
                // FillFilterHostList();
                // FillFilterHostMacList();
            }
        }
    };
    xhr.send();

}

//获取开启DNSfilter功能的switch列表
function FillEnableList(){
	var xhr=getXMLHttpRequest();
	xhr.open("GET","/getswfilterset",true);
	xhr.onreadystatechange=function(){
		if(xhr.readyState==4){
			if(xhr.status==200){
				var MyObject = JSON.parse(xhr.responseText);
				
				for(var i=0;i<MyObject.length;i++){
					//alert(MyObject[i].swid);
						var enable_form = document.getElementById("enablelist");
		            var Check = document.createElement("input");
		            Check.name="enablecheckbox";
		            Check.value=MyObject[i].swid;
		            Check.type="checkbox";            
		            enable_form.appendChild(Check);
		            enable_form.innerHTML+=MyObject[i].swid+"<br/>";
		           			
				}
				 FillDisableList(MyObject);			
				}
				
			}
		};
		xhr.send(null);
}
//获取未开启DNSfilter功能的switch列表
function FillDisableList(enablelist){
	var xhr=getXMLHttpRequest();
	xhr.open("GET","/getswinfo",true);
	xhr.onreadystatechange=function(){
		if(xhr.readyState==4){
			if(xhr.status==200){
				var MyObject = JSON.parse(xhr.responseText);
				
				for(var i=0;i<MyObject.length;i++){
						//alert(MyObject[i].switchDPID);
						var has=false;
						for(var j=0;j<enablelist.length;j++)
							if(enablelist[j].swid==MyObject[i].switchDPID)
								has=true;
						if(has)
							continue;
						
						var enable_form = document.getElementById("disablelist");
						//enable_form.innerHTML+="disable<br />"
		            var Check = document.createElement("input");
		            Check.name="disablecheckbox";
		            Check.value=MyObject[i].switchDPID;
		            Check.type="checkbox";            
		            enable_form.appendChild(Check);
		            enable_form.innerHTML+=MyObject[i].switchDPID+"<br/>";
		            			
				}				
				}
				
			}
		};
		xhr.send(null);
}

//处理DNSFilter 黑名单的操作。
function SubblacklistForm() {
	var xhr=getXMLHttpRequest();
	var newblack=new Object();
	if(document.blackdomain.domain1.value=="")
	{
		alert("DomainName cannot be empty");
		return;
	}
	newblack.domainname=document.blackdomain.domain1.value;
	
	var ratio=document.getElementsByName("domain_action");
	for(var i=0;i<ratio.length;i++)
		if(ratio[i].checked)
		{
			newblack.action=ratio[i].value;
			break;
		}
		
	var xhr=getXMLHttpRequest();
	xhr.open("POST","/blacklist",true);
	xhr.onreadystatechange=function(){
		if(xhr.readyState==4){
			if(xhr.status==200){				
					var json=JSON.parse(xhr.responseText);
					alert(json.details);				
				}
			}
		};
		xhr.send(JSON.stringify(newblack));

}

//处理DNSFilter 白名单的操作。
function SubwhitelistForm() {
	var xhr=getXMLHttpRequest();
	var newwhite=new Object();
	if(document.whitedomain.domain2.value=="")
	{
		alert("DomainName cannot be empty");
		return;
	}
	newwhite.domainname=document.whitedomain.domain2.value;
	
	var ratio=document.getElementsByName("domain_action");
	for(var i=0;i<ratio.length;i++)
		if(ratio[i].checked)
		{
			newwhite.action=ratio[i].value;
			break;
		}
		
	var xhr=getXMLHttpRequest();
	xhr.open("POST","/whitelist",true);
	xhr.onreadystatechange=function(){
		if(xhr.readyState==4){
			if(xhr.status==200){				
					var json=JSON.parse(xhr.responseText);
					alert(json.details);				
				}
			}
		};
		xhr.send(JSON.stringify(newwhite));

}

//关闭被选中的Switch的DNSfilter功能
function disablesw() {
	var checkbox=document.getElementsByName("enablecheckbox");
	var count=0;
	for(var i=0;i<checkbox.length;i++)
		if(checkbox[i].checked)
		{
			//alert(checkbox[i].value);
			swswitch(checkbox[i].value,"disable");
			count=count+1;
			
		}
	if(count>0){
		alert("remove success");
		ManageDNSFilter();
	}
}
//开启被选中的Switch的DNSfilter功能
function enablesw() {
	
	var checkbox=document.getElementsByName("disablecheckbox");
	var count=0;
	for(var i=0;i<checkbox.length;i++)
		if(checkbox[i].checked)
		{
			//alert(checkbox[i].value);
			swswitch(checkbox[i].value,"enable");
			count=count+1;
		}
	if(count>0){
		alert("add success");
		ManageDNSFilter();
	}

}
//发送Switch的DNSfilter开启或关闭命令到后台。
function swswitch(swid,action){
	var json=new Object();
	json.swid=swid;
	json.action=action;
	var xhr=getXMLHttpRequest();
	xhr.open("POST","/dnsfilterswitch",true);
	xhr.onreadystatechange=function(){
		if(xhr.readyState==4){
			if(xhr.status==200){				
					//var json=JSON.parse(xhr.responseText);
					//alert(json);				
				}
			}
		};
		xhr.send(JSON.stringify(json));

}

function	getDNSRedirectIP(){	
	var xhr=getXMLHttpRequest();
	xhr.open("GET","/getdnsredirectip",true);
	xhr.onreadystatechange=function(){
		if(xhr.readyState==4){
			if(xhr.status==200){
								
					var json=JSON.parse(xhr.responseText);
					var tmp = document.getElementById("cur_DNSRedirect_ip");
					tmp.value=json.dnsredirectip;
				
				}}};
		xhr.send(null);
}

function	setDNSRedirectIP(){
	var json=new Object();
	var tmp = document.getElementById("new_DNSRedirect_ip");
	if(tmp.value==""){
		return;
	}
	json.dnsredirectip=tmp.value;
	
	var xhr=getXMLHttpRequest();
	xhr.open("post","/setdnsredirectip",true);
	xhr.onreadystatechange=function(){
		if(xhr.readyState==4){
			if(xhr.status==200){
					
					alert(xhr.responseText);
					ManageDNSFilter();					
				}}};
		xhr.send(JSON.stringify(json));
}


function FillFilterHostList(){
	var xhr=getXMLHttpRequest();
	xhr.open("GET","/gethostfilterset",true);
	xhr.onreadystatechange=function(){
		if(xhr.readyState==4){
			if(xhr.status==200){
				var MyObject = JSON.parse(xhr.responseText);
				
				for(var i=0;i<MyObject.length;i++){
					
					var enable_form = document.getElementById("filterhostlist");
		            var Check = document.createElement("input");
		            Check.name="filterhostcheckbox";
		            
		            Check.value=MyObject[i].host_ip;
		            Check.type="checkbox";            
		            enable_form.appendChild(Check);
		            enable_form.innerHTML+=MyObject[i].host_ip+"<br/>";
		           			
				}		
				}
				
			}
		};
		xhr.send(null);
}

function FillFilterHostMacList(){
	var xhr=getXMLHttpRequest();
	xhr.open("GET","/gethostmacfilterset",true);
	xhr.onreadystatechange=function(){
		if(xhr.readyState==4){
			if(xhr.status==200){
				var MyObject = JSON.parse(xhr.responseText);
				
				for(var i=0;i<MyObject.length;i++){
					
					var enable_form = document.getElementById("filterhostmaclist");
		            var Check = document.createElement("input");
		            Check.name="filterhostmaccheckbox";
		            alert(Check.name);
		            Check.value=MyObject[i].host_mac;
		            Check.type="checkbox";            
		            enable_form.appendChild(Check);
		            enable_form.innerHTML+=MyObject[i].host_mac+"<br/>";
		           			
				}		
				}
				
			}
		};
		xhr.send(null);
}

function addfilterhost(){
	
	var host = document.getElementById("filterhost");
	if(host.value==""){
		return;
	}
	setfilterhost(host.value,"enable");
	
}

function addfilterhostmac(){
	
	var host = document.getElementById("filterhostmac");
	if(host.value==""){
		return;
	}
	setfilterhostmac(host.value,"enable");
	
}

function removefilterhost() {
	var checkbox=document.getElementsByName("filterhostcheckbox");
	var count=0;
	for(var i=0;i<checkbox.length;i++)
		if(checkbox[i].checked)
		{
			//alert(checkbox[i].value);
			setfilterhost(checkbox[i].value,"disable");
			count=count+1;
		}
	if(count>0){
		alert("Remove success");
		ManageDNSFilter();
	}
	
}

function removefilterhostmac() {
	var checkbox=document.getElementsByName("filterhostmaccheckbox");
	var count=0;
	for(var i=0;i<checkbox.length;i++)
		if(checkbox[i].checked)
		{
			//alert(checkbox[i].value);
			setfilterhostmac(checkbox[i].value,"disable");
			count=count+1;
		}
	if(count>0){
		alert("Remove success");
		ManageDNSFilter();
	}
	
}

function setfilterhost(host_ip,action){
	
	var json=new Object();
	
	json.host_ip=host_ip;
	json.action=action;
	var xhr=getXMLHttpRequest();
	xhr.open("post","/setfilterhost",true);
	xhr.onreadystatechange=function(){
		if(xhr.readyState==4){
			if(xhr.status==200){
					if(action=="enable"){
						alert(xhr.responseText);
						ManageDNSFilter();
					}

				}}};
				
		xhr.send(JSON.stringify(json));
}

function setfilterhostmac(host_mac,action){
	
	var json=new Object();
	
	json.host_mac=host_mac;
	json.action=action;
	var xhr=getXMLHttpRequest();
	xhr.open("post","/setfilterhostmac",true);
	xhr.onreadystatechange=function(){
		if(xhr.readyState==4){
			if(xhr.status==200){
					if(action=="enable"){
						alert(xhr.responseText);
						ManageDNSFilter();
					}

				}}};
				
		xhr.send(JSON.stringify(json));
}

function SubExtractFeatureForm() {
    var xhr=getXMLHttpRequest();
    var newfeature=new Object();
    if(document.feature.sourcefile.value=="")
    {
        alert("filename cannot be empty");
        return;
    }
    newfeature.sourcefile=document.feature.sourcefile.value;
    console.log(newfeature);

    var ratio1=document.getElementsByName("feature_type");
    var ratio2=document.getElementsByName("feature_label");
    for(var i=0;i<ratio1.length;i++)
        if(ratio1[i].checked)
        {
            newfeature.type=ratio1[i].value;
            break;
        }
    for(var i=0;i<ratio2.length;i++)
        if(ratio2[i].checked)
        {
            newfeature.label=ratio2[i].value;
            break;
        }

    var xhr=getXMLHttpRequest();
    xhr.open("POST","/feature",true);
    xhr.onreadystatechange=function(){
        if(xhr.readyState==4){
            if(xhr.status==200){
                var json=JSON.parse(xhr.responseText);
                alert(json.details);
                Classifier();
            }
        }
    };
    xhr.send(JSON.stringify(newfeature));

}

function SubTrainForm() {
    var xhr=getXMLHttpRequest();
    var newtrain=new Object();
    if(document.train.trainfile.value=="")
    {
        alert("filename cannot be empty");
        return;
    }
    newtrain.trainfile=document.train.trainfile.value;

    var xhr=getXMLHttpRequest();
    xhr.open("POST","/train",true);
    xhr.onreadystatechange=function(){
        if(xhr.readyState==4){
            if(xhr.status==200){
                var json=JSON.parse(xhr.responseText);
                alert(json.details);
                Classifier();
            }
        }
    };
    xhr.send(JSON.stringify(newtrain));

}

function SubTestForm() {
    var xhr=getXMLHttpRequest();
    var newtest=new Object();
    if(document.test.testfile.value=="" || document.test.modelfile.value=="" )
    {
        alert("filename cannot be empty");
        return;
    }
    newtest.testfile=document.test.testfile.value;
    newtest.modelfile=document.test.modelfile.value;

    var xhr=getXMLHttpRequest();
    xhr.open("POST","/test",true);
    xhr.onreadystatechange=function(){
        if(xhr.readyState==4){
            if(xhr.status==200){
                var json=JSON.parse(xhr.responseText);
                alert(json.details);
               Classifier();
            }
        }
    };
    xhr.send(JSON.stringify(newtest));

}

function SubPredictForm() {
    var xhr=getXMLHttpRequest();
    var newpredict=new Object();
    if(document.predict.domain.value=="" )
    {
        alert("domainname cannot be empty");
        return;
    }
    newpredict.domain=document.predict.domain.value;

    var xhr=getXMLHttpRequest();
    xhr.open("POST","/predict",true);
    xhr.onreadystatechange=function(){
        if(xhr.readyState==4){
            if(xhr.status==200){
                var json=JSON.parse(xhr.responseText);
                alert(json.details);
                Classifier();
            }
        }
    };
    xhr.send(JSON.stringify(newpredict));

}