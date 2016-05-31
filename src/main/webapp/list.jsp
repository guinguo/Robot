<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>下载</title>
    <script type="text/javascript" src="${pageContext.request.contextPath}/front/js/jquery-2.1.1.min.js"></script>
    <style type="text/css">
      table {
        position: relative;
        left: 30%;
        top: 10%;
        padding: 10px;
      }
      table tr {
      }
      table td {
        padding: 10px 15px;
      }
    </style>
</head>
<body>
<table id="dynamic-table" class="my-table table table-striped table-bordered table-hover">
  <thead>
  <tr>
    <th class="center">文件名称</th>
    <th class="center">操作</th>
  </tr>
  </thead>
  <tbody>
  <c:forEach items="${list}" var="file">
    <tr id="${file}">
      <td style="text-align:left;">${file}</td>
      <td>
        <div class="btn-group">
          <button class="btn-del" data-name="${file}">
            <i class="ace-icon fa fa-trash-o bigger-120"></i>
            删除
          </button>
          <a href="${pageContext.request.contextPath}/download?name=${file}"
             class="btn btn-primary btn-download ">
            <i class="ace-icon fa fa-download bigger-120"></i>
            下载
          </a>
        </div>
      </td>

    </tr>
  </c:forEach>
  </tbody>
</table>
<h1><a href="${pageContext.request.contextPath}/a.jsp">去上传</a> </h1>
<script type="text/javascript">
  $(function () {
    $(".btn-del").click(del)
  });
  function del() {
    if(confirm("你确认要删除该文件吗？？")){
      var $name = $(this).attr("data-name");
      $.ajax({
              type: "POST",
              url: "${pageContext.request.contextPath}/delete",
              data: "name="+$name,
              success: function (result) {
                alert(result);
                location.reload();
              }
      })
    }
  }

</script>
</body>
</html>
