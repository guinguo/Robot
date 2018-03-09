<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<html lang="zh-cn">
<head>
    <title>微博数据分析</title>
    <meta charset="UTF-8">
    <%@include file="/inc/header.jsp" %>
</head>
<body>
<div class="home-page">
    <%@include file="/inc/top.jsp" %>
    <div class="home-content">
        <div class="content-title clearfix">
            <p>用户画像分析</p>
        </div>
        <div class="content-search">
            <form action="${pageContext.request.contextPath}/user_profile/search">
                <div class="btn-group" role="group" aria-label="...">
                    <ul class="tit-tab clearfix">
                        <li><a data-type="1" class="${dataAll eq 1 ? 'current l1' : 'current l1'}" onclick="submit(1)">全部</a></li>
                        <li><a data-type="2" class="${dataAll eq 1 ? 'current l2' : 'l2'}" onclick="submit(2)">已完成</a></li>
                        <li><a data-type="3" class="${dataAll eq 1 ? 'current l3' : 'l3'}" onclick="submit(3)">进行中</a></li>
                    </ul>
                    <%--<div class="kolsx`tionsSearch()">查询</a>
                    </div>--%>
                </div>
            </form>
        </div>
        <div class="task_list">
            <ul id="taskList" class="thumbnail-list clearfix" style="height: auto;">
                <li id="newTask_add" class="thumbnail-box clearfix">
                    <a class="thumbnail-newtask"><span></span><p>添加任务</p></a>
                </li>
                <li id="taskLi12868" class="thumbnail-box clearfix"><a target="_blank" href="../community/detail.do?taskId=12868&amp;projectId=128999"></a>
                    <div class="box3">
                        <a target="_blank" href="../community/detail.do?taskId=12868&amp;projectId=128999">
                            <div class="thumbnail-left">
                                <div class="circle-text">
                                    <%--<span>646用户</span>--%>
                                    <img src="http://tp3.sinaimg.cn/1812577840/180/40004332098">
                                </div>
                                <p>已完成 <b class="pink">100%</b></p>
                            </div>
                            <div class="thumbnail-right">
                                <div class="user-name clearfix">
                                    <b title="落染Vanessa">落染Vane...
                                        <img src="${pageContext.request.contextPath}/front/img/dr.png">
                                    </b>
                                </div>
                                <dl class="user-profile">
                                    <dt><span>关注：</span>168</dt>
                                    <dt><span>粉丝：</span>2445</dt>
                                    <dt><span>地区：</span>其他</dt>
                                    <dt>2017-11-08 09:12</dt>
                                </dl>
                            </div></a>
                        <a tid="12868" class="guanbi"></a>
                    </div></li>
            </ul>
        </div>
    </div>
    <%@include file="/inc/bottom.jsp" %>
</div>
<div times="2" showtime="0" id="xubox_layer2" class="xubox_layer" type="page">
    <div class="xubox_main">
        <div class="xubox_page">
            <div id="step_one1" class="tcc tdd layer_pageContent" style="display: block;">
                <div class="tcc-tit">用户画像：选择用户<a class="guanbi"></a></div>
                <!--内容-->
                <input class="search_form ng-valid ng-dirty ng-valid-parse ng-touched" id="searchInput"
                       placeholder="请搜索新浪微博昵称" ng-focus="showSearchContent()" ng-model="searchNameModel">

                <div class="search_content" id="searchList" ng-show="searchContent">
                    <div class="search_content_area">
                        <p class="p01 grey6 fs12">
                            <span class="down_arrow"></span>请在下方搜索结果中点击微博昵称，开始画像~
                        </p>
                        <ul class="dashed_ul">

                            <li class="clearfix border_dashed ng-scope" ng-repeat="user in userData" ng-click="checkUser(user)">
                                <div class="fang_user_list">
                                    <p class="user_list_weixin clearfix"><span class="header_02"><img width="40" height="40" ng-src="http://tp1.sinaimg.cn/1722656062/50/5697960275/1" src="http://tp1.sinaimg.cn/1722656062/50/5697960275/1"></span>
                                    </p>
                                    <i style="height: 16px;" ng-bind-html="user|showTypeImg" class="ng-binding">Karen莫文蔚<img src="${pageContext.request.contextPath}/front/img/hv.png" style="height: 16px; width: 16px;"></i>
                                    <br>
                                    <div class="clearfix bean_vermicelli">
                                        <b ng-class="{'boy_tb':user.gender=='M','girl_tb':user.gender=='F'}" class="girl_tb"></b>
                                        <em class="ng-binding">粉丝数：13728064</em><em class="location_place ng-binding">香港</em>
                                    </div>
                                </div>
                            </li>

                            <li class="clearfix border_dashed ng-scope" ng-repeat="user in userData" ng-click="checkUser(user)">
                                <div class="fang_user_list">
                                    <p class="user_list_weixin clearfix"><span class="header_02"><img width="40" height="40" ng-src="http://tp1.sinaimg.cn/1431308884/50/5697960275/1" src="http://tp1.sinaimg.cn/1431308884/50/5697960275/1"></span>
                                    </p>
                                    <i style="height: 16px;" ng-bind-html="user|showTypeImg" class="ng-binding">快乐大本营<img src="${pageContext.request.contextPath}/front/img/lv.png" style="height: 16px; width: 16px;"></i>
                                    <br>
                                    <div class="clearfix bean_vermicelli">
                                        <b ng-class="{'boy_tb':user.gender=='M','girl_tb':user.gender=='F'}" class="boy_tb"></b>
                                        <em class="ng-binding">粉丝数：11499068</em><em class="location_place ng-binding">湖南 长沙</em>
                                    </div>
                                </div>
                            </li>

                            <li class="clearfix border_dashed ng-scope" ng-repeat="user in userData" ng-click="checkUser(user)">
                                <div class="fang_user_list">
                                    <p class="user_list_weixin clearfix"><span class="header_02"><img width="40" height="40" ng-src="http://tp1.sinaimg.cn/5212808549/50/5697960275/1" src="http://tp1.sinaimg.cn/5212808549/50/5697960275/1"></span>
                                    </p>
                                    <i style="height: 16px;" ng-bind-html="user|showTypeImg" class="ng-binding">快滴发红包1314</i>
                                    <br>
                                    <div class="clearfix bean_vermicelli">
                                        <b ng-class="{'boy_tb':user.gender=='M','girl_tb':user.gender=='F'}" class="girl_tb"></b>
                                        <em class="ng-binding">粉丝数：2874</em><em class="location_place ng-binding">海外 美国</em>
                                    </div>
                                </div>
                            </li>

                        </ul>
                        <div class="search_page p20 clearfix">
                            <a class="fr pg_next small_button" ng-show="hasNext&amp;&amp;pageInfo.pageNum>1"
                               ng-click="getNextPage()">下一页</a>
                            <a class="fr pg_last mr10 small_button" ng-show="hasLast&amp;&amp;pageInfo.pageNum>1"
                               ng-click="getLastPage()">上一页</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <span class="xubox_setwin"></span><span class="xubox_botton"></span></div>
</div>
<div times="1" id="xubox_shade1" class="xubox_shade"
     style="z-index:19891015;background-color:#000;opacity:0.3;filter:alpha(opacity=30);"></div>
<%@include file="/inc/footer.jsp"%>
</body>
</html>
