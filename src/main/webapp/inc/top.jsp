<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<div class="common-top">
    <div class="common-logo">
        <a href="/index.jsp">
            <img src="${pageContext.request.contextPath}/front/img/logo.png">
        </a>
    </div>
    <span class="common-title"><i class="line"></i>用户画像</span>
    <div class="common-tools">
        <div class="common-nav">
            <p class="clearfix">
                <i class="common-nav-current"></i>
                ${currentNav}用户画像
            </p>
            <ul>
                <li><a href="#">用户画像</a></li>
                <%--<li><a href="#">社群画像</a></li>--%>
            </ul>
        </div>
        <div class="common-profile" style="display: none;">
            <em></em>
        </div>
        <div>
            <span>mail</span>
            <span>${resultCount}11</span>
        </div>
    </div>
</div>