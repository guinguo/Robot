<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ page import="
    java.util.*"
        %>
<html lang="zh-cn">
<head>
  <title>data</title>
  <meta http-equiv="content-type" content="text/html; charset=UTF-8">
   <style type="text/css">
		a {
				text-decoration:none;
				color: #616161;
		}
		div.div-upload {
				float:left;
		}
		#div-left {
				width:50%;
		}
   </style>
</head>
<body class="no-skin">
<div style="margin-top:65px;margin-left:65px;align:center">
  <form>
    <input type="text" name="data" style="length:200px">
    <input type="submit"/>
  </form>
</div>
<div class="div-upload" id="div-left">
<%
  String data=request.getParameter("data");
  String rm = request.getParameter("rm");
  int index = 0;
  List<String> datas = (List<String>) application.getAttribute("datas");
  if(datas==null) datas = new ArrayList();
  if(data!=null&&data.length()>0){  
	  datas.add(data);
  }
  if(rm!=null&&rm.length()>0&&datas.size()>0){
	  index = Integer.parseInt(rm);
	  if(index<datas.size()){
		datas.remove(index);
	  }
  }
  application.setAttribute("datas",datas);
  out.println("<tr>");
  for(int i=datas.size()-1;i>=0;i--){
    out.println("<td>"+datas.get(i)+"</td>"
	+"&nbsp;&nbsp;&nbsp;<td><a href='http://guinguo.top/ScauSky/a.jsp?rm="+i+"'>"+"remove"+"</a></td><br>");
  }
  out.println("</tr>");
%>
</div>
<div class="div-upload"  id="div-right">
	<form id="file-add" method="post" enctype="multipart/form-data"
                              action="${pageContext.request.contextPath}/file" >
    <input type="file" name="file" style="length:200px">
    <input type="submit" value="上传"/>&nbsp;&nbsp;&nbsp;&nbsp;
	<a href="${pageContext.request.contextPath}/list" >去下载</a>
  </form>
 
</div>
</body>
</html>

