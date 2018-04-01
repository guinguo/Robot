<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<div class="common-top">
    <div class="common-logo">
        <a href="${pageContext.request.contextPath}/">
            <img src="${pageContext.request.contextPath}/front/img/logo.png">
        </a>
    </div>
    <span class="common-title"><i class="line"></i>用户画像</span>
    <div class="common-tools">
        <div class="common-nav">
            <%--<p class="clearfix common-nav-title">
                <i class="common-nav-current"></i>
                <span>${currentNav}用户画像</span>
            </p>
            <ul class="common-navs">
                <li><a href="#">用户画像</a></li>
                &lt;%&ndash;<li><a href="#">社群画像</a></li>&ndash;%&gt;
            </ul>--%>
            <select class="common-select" onchange="window.location.href=this.value">
                <option class="common-option" value="${pageContext.request.contextPath}/index">用户画像</option>
                <option class="common-option" value="${pageContext.request.contextPath}/group_profile">社群画像</option>
            </select>
        </div>
        <div class="common-profile" style="display: none;">
            <em></em>
        </div>
        <a href="${pageContext.request.contextPath}/user_profile" class="common-top-messages">
            <span class="common-top-messages-namber">${resultCount}11</span>
        </a>
    </div>
</div>