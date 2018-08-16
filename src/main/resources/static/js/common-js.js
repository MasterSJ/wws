/**退出登录*/
function logOut(){
	if(confirm("退出登录?")) {
		window.location.href="/logOut";
	}
}
/**获取url中的name参数值*/
function getUrlParam(name)
{
     var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
     var r = window.location.search.substr(1).match(reg);
     if(r!=null) {
    	 return  unescape(r[2]); 
     }
     return null;
     
}


/**新打开一个模态框*/
function openModalPage(title, url){
    var fatherBody = $(window.top.document.body);
    var dataBody = $("#data_list" , fatherBody);
    var nagBody = $("#nag_list" , fatherBody);
    nagBody.append("<li id='"+100+"nag'><div class='nag_title'><p>"+title+"</p></div><span class='close icon-cross'></span></li>");
    dataBody.append("<li><div class='loding'></div><iframe src='"+url+"' frameborder='0' onload='ifram_onload(this)'></iframe></li>");
    $("li",nagBody).eq(-1).trigger("click");
}




//显示数据加载提示窗口  
function showDataLoadingTip(parentWindow) {  
    var msgw, msgh, bordercolor;  
    msgw = 400; //提示窗口的宽度   
    msgh = 100; //提示窗口的高度   
    bordercolor = "#336699"; //提示窗口的边框颜色   
    titlecolor = "#99CCFF"; //提示窗口的标题颜色   
  
    var sWidth, sHeight;  
    //sWidth = window.screen.availWidth;  
    //sHeight = window.screen.availHeight;  
    sWidth = parentWindow.body.clientWidth;
    sHeight = parentWindow.body.clientHeight;
  
    var bgObj = parentWindow.createElement("div");  
    bgObj.setAttribute('id', 'bgDiv');  
    bgObj.style.position = "absolute";  
    bgObj.style.top = "0";  
    bgObj.style.background = "#777";  
    bgObj.style.filter = "progid:DXImageTransform.Microsoft.Alpha(style=3,opacity=25,finishOpacity=75";  
    bgObj.style.opacity = "0";  
    bgObj.style.left = "0";  
    bgObj.style.width = sWidth + "px";  
    bgObj.style.height = sHeight + "px";  
    parentWindow.body.appendChild(bgObj);  
    var msgObj = parentWindow.createElement("div");  
    msgObj.setAttribute("id", "msgDiv");  
    msgObj.setAttribute("align", "center");  
    msgObj.style.position = "absolute";  
    msgObj.style.background = "white";  
    msgObj.style.font = "12px/1.6em Verdana, Geneva, Arial, Helvetica, sans-serif";  
    msgObj.style.border = "1px solid " + bordercolor;  
    msgObj.style.width = msgw + "px";  
    msgObj.style.height = msgh + "px";  
    msgObj.style.top = (parentWindow.documentElement.scrollTop + (sHeight - msgh ) / 2 ) + "px";  
    //msgObj.style.top = "170px";
    msgObj.style.left = (sWidth - msgw) / 2 + "px";  
    var title = parentWindow.createElement("h4");  
    var bigImg = parentWindow.createElement("img");
    bigImg.src="/icon/loading.gif";   //给img元素的src属性赋值    
    msgObj.appendChild(bigImg);      //为dom添加子元素img 
    title.setAttribute("id", "msgTitle");  
    title.setAttribute("align", "right");  
    title.style.margin = "0";  
    title.style.padding = "3px";  
    title.style.background = "#319db5";  
    title.style.filter = "progid:DXImageTransform.Microsoft.Alpha(startX=20, startY=20, finishX=100, finishY=100,style=1,opacity=75,finishOpacity=100);";  
    title.style.opacity = "0.75";  
    title.style.border = "1px solid " + bordercolor;  
    title.style.height = "18px";  
    title.style.font = "12px Verdana, Geneva, Arial, Helvetica, sans-serif";  
    title.style.color = "white";  
    //title.style.cursor = "pointer";  
    //title.innerHTML = "关闭";  
    //title.onclick = closediv;  
    parentWindow.body.appendChild(msgObj);  
    parentWindow.getElementById("msgDiv").appendChild(title);  
    var txt = parentWindow.createElement("p");  
    txt.style.margin = "1em 0";  
    txt.setAttribute("id", "msgTxt");  
    txt.innerHTML = "正在努力加载数据中，请稍等……";  
    parentWindow.getElementById("msgDiv").appendChild(txt);  
}

//调整数据加载提示窗口大小，自适应父窗口居中显示
function resizeDataLoadingTip(parentWindow) {
	if(parentWindow.getElementById("bgDiv")){
		var sWidth = parentWindow.body.clientWidth;
	    var sHeight = parentWindow.body.clientHeight;
		var msgObj = parentWindow.getElementById("msgDiv");
		var msgw = 400;
		var msgh = 100;
		msgObj.style.left = (sWidth - msgw) / 2 + "px"; 
		msgObj.style.top = (parentWindow.documentElement.scrollTop + (sHeight - msgh) / 2 ) + "px"; 
	}
}

//关闭数据加载提示窗口
function closeDataLoadingTip(parentWindow) {  
    //Close Div   
    //var a = parentWindow.getElementById("bgDiv");
    // console.info(a);
    if(parentWindow.getElementById("bgDiv")){
    	parentWindow.body.removeChild(parentWindow.getElementById("bgDiv"));  
	    parentWindow.getElementById("msgDiv").removeChild(parentWindow.getElementById("msgTitle"));  
	    parentWindow.body.removeChild(parentWindow.getElementById("msgDiv"));
    }
}