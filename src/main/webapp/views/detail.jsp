<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page isELIgnored="false" %>
<html lang="zh-cn">
<head>
    <title>微博数据分析</title>
    <meta charset="UTF-8">
    <%@include file="/inc/header.jsp" %>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/front/css/detail.css" />
</head>
<body>
<div class="home-page">
    <%@include file="/inc/top.jsp" %>
    <div class="home-content">
        <div class="mb20 clearfix">
            <div class="yhhx">
                <div class="rwhx"><img width="400" height="550" src=${pageContext.request.contextPath}${result.profile.sex == '男' ? "/front/img/m25.gif":"/front/img/f25.gif"}></div>
                <!--浮动信息层1-->
                <div class="yhzlmk1" style="position:absolute; left:180px; top:120px;">
                    <div class="yhhxzl" ng-click="dtp.userDimensionInfo.event.clickInfoModel('preferWords')">
                        <div class="p010">
                            <div class="kolhxbt clearfix">我最爱说</div>
                            <ul>
                                <!-- ngRepeat: preferWordsInfo in dtp.userDimensionInfo.preferWordsInfo.list --><li class="ng-binding ng-scope" ng-repeat="preferWordsInfo in dtp.userDimensionInfo.preferWordsInfo.list" ng-bind="preferWordsInfo" ng-show="dtp.userDimensionInfo.preferWordsInfo.taskHasDone">转发</li><!-- end ngRepeat: preferWordsInfo in dtp.userDimensionInfo.preferWordsInfo.list --><li class="ng-binding ng-scope" ng-repeat="preferWordsInfo in dtp.userDimensionInfo.preferWordsInfo.list" ng-bind="preferWordsInfo" ng-show="dtp.userDimensionInfo.preferWordsInfo.taskHasDone">娜娜</li><!-- end ngRepeat: preferWordsInfo in dtp.userDimensionInfo.preferWordsInfo.list --><li class="ng-binding ng-scope" ng-repeat="preferWordsInfo in dtp.userDimensionInfo.preferWordsInfo.list" ng-bind="preferWordsInfo" ng-show="dtp.userDimensionInfo.preferWordsInfo.taskHasDone">una</li><!-- end ngRepeat: preferWordsInfo in dtp.userDimensionInfo.preferWordsInfo.list -->

                                <li class="ng-hide" ng-show="!dtp.userDimensionInfo.preferWordsInfo.taskHasDone">
                                    <em class="grey">小蓝鲸正在分析TA最爱说，
                                        <a class="refresh_btn pink" onclick="location.reload(true)">刷新</a>看看好了没~
                                    </em>
                                </li>

                            </ul>
                            <p><a class="kolhxgd"></a></p>
                        </div>
                    </div>
                    <div class="dian"><img src=${pageContext.request.contextPath}/front/img/dian.png width="40" height="40" alt=""></div>
                </div>
                <!--浮动信息层2-->
                <div class="yhzlmk3" style="position:absolute; left:80px; top:350px;">
                    <div class="yhhxzl" ng-click="dtp.userDimensionInfo.event.clickInfoModel('myFans')">
                        <div class="p010">
                            <div class="kolhxbt clearfix"><a class="kolhxgd"></a>我的粉丝</div>
                            <p class="" ng-show="dtp.userDimensionInfo.myFansInfo.taskHasDone">集中在：<span class="colorcc3366 ng-binding" ng-bind="dtp.userDimensionInfo.myFansInfo.concentrateUpon">广东</span><i></i><i>|</i><i></i>更多：<span class="colorcc3366 ng-binding" ng-bind="dtp.userDimensionInfo.myFansInfo.more">男</span>
                            </p>

                            <p class="" ng-show="dtp.userDimensionInfo.myFansInfo.taskHasDone">TA们喜爱：



                                <span class="colorcc3366 ng-binding" ng-bind="dtp.userDimensionInfo.myFansInfo.whatAreTheyLike.cutText">搞笑段子 漫画 欧...</span>
                            </p>

                            <p class="ng-hide" ng-show="!dtp.userDimensionInfo.myFansInfo.taskHasDone"><em class="grey">小蓝鲸正在研究TA的粉丝，<a class="refresh_btn pink" onclick="location.reload(true)">刷新</a>看看好了没~</em></p>
                        </div>
                    </div>
                    <div class="dian"><img src=${pageContext.request.contextPath}/front/img/dian.png width="40" height="40" alt=""></div>
                </div>
                <!--浮动信息层3-->
                <div class="yhzlmk4" style="position:absolute; left:550px; top:150px;">
                    <div class="dian"><img src=${pageContext.request.contextPath}/front/img/dian.png width="40" height="40" alt=""></div>
                    <div class="yhhxzl" ng-click="dtp.userDimensionInfo.event.clickInfoModel('myInterests')">
                        <div class="p010">
                            <div class="kolhxbt clearfix"><a class="kolhxgd"></a>我的兴趣</div>
                            <p class="" ng-show="dtp.userDimensionInfo.myInterestsInfo.taskHasDone">
                                <span class="colorcc3366 ng-binding" ng-bind="dtp.userDimensionInfo.myInterestsInfo.cutText">随手拍 cosplay 人物摄...</span>
                            </p>

                            <p class="ng-hide" ng-show="!dtp.userDimensionInfo.myInterestsInfo.taskHasDone"><em class="grey">小蓝鲸正在挖掘TA的兴趣，<a class="refresh_btn pink" onclick="location.reload(true)">刷新</a>看看好了没~</em></p>

                        </div>
                    </div>
                </div>
            </div>
            <!--右侧用户详细资料-->
            <div class="w260 fr">
                <div class="yhzl">
                    <div class="mb20 clearfix position_relative">
                        <div class="yhtx"><img src="${result.profile.avatar}"></div>
                        <ul class="yhwbcs">
                            <li>粉丝：<b ng-bind="dtp.userPropertyAbout.about.cntFollowers" class="ng-binding">${result.profile.fans}</b></li>
                            <li>关注：<b ng-bind="dtp.userPropertyAbout.about.cntFollowing" class="ng-binding">${result.profile.focus}</b></li>
                            <li>微博：<b ng-bind="dtp.userPropertyAbout.about.cntPosts" class="ng-binding">${result.profile.blogNumber}</b></li>
                        </ul>
                    </div>
                    <table class="yhzllb" width="100%" border="0" cellspacing="0" cellpadding="0">
                        <tbody>
                        <tr>
                            <td class="yhzllbl">用户昵称：</td>
                            <td class="yhzllbr"><b><a href="http://weibo.com/u/1898267100" target="_blank">${result.profile.nickname}</a></b>
                            </td>
                        </tr>
                        <tr>
                            <td class="yhzllbl">所在区域：</td>
                            <td class="yhzllbr"><b ng-bind="dtp.userPropertyAbout.about.txtLocation" class="ng-binding">${result.profile.address}</b></td>
                        </tr>
                        <tr>
                            <td class="yhzllbl" valign="top">会员等级：</td>
                            <td class="yhzllbr ng-binding" ng-bind="dtp.userPropertyAbout.about.verifiedStr">
                                ${result.profile.member > 0 ? '会员V' : '普通用户'}
                                ${result.profile.member > 0 ? result.profile.member : ''}
                            </td>
                        </tr>
                        <tr>
                            <td class="yhzllbl">个人签名：</td>
                            <td class="yhzllbr ng-binding" ng-bind="dtp.userPropertyAbout.about.txtDescription">${result.profile.intro}</td>
                        </tr>
                        <tr>
                            <td class="yhzllbl" style="vertical-align: top;">信用级别：</td>
                            <td class="yhzllbr ng-binding" ng-bind="dtp.userPropertyAbout.about.userTags">${result.profile.credit}</td>
                        </tr>
                        <c:if test="${ not empty result.profile.school }">
                            <tr>
                                <td class="yhzllbl" style="vertical-align: top;">学校：</td>
                                <td class="yhzllbr ng-binding" ng-bind="dtp.userPropertyAbout.about.userTags">${result.profile.school}</td>
                            </tr>
                        </c:if>
                        <c:if test="${ not empty result.profile.company }">
                            <tr>
                                <td class="yhzllbl" style="vertical-align: top;">公司：</td>
                                <td class="yhzllbr ng-binding" ng-bind="dtp.userPropertyAbout.about.userTags">${result.profile.company}</td>
                            </tr>
                        </c:if>
                        </tbody></table>
                </div>
            </div>
        </div>
        <div class="charts"></div>
    </div>
    <%@include file="/inc/bottom.jsp" %>
</div>
<%@include file="/inc/footer.jsp"%>
</body>
</html>
