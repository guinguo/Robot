<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page isELIgnored="false" %>
<html lang="zh-cn">
<head>
    <title>微博数据分析</title>
    <meta charset="UTF-8">
    <%@include file="/inc/header.jsp" %>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/front/css/detail.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/front/css/kol.css" />
</head>
<body>
<div class="detail-page">
    <%@include file="/inc/top.jsp" %>
    <div class="detail-content">
        <div class="mb20 clearfix">
            <div class="yhhx">
                <div class="rwhx"><img width="400" height="550" src=${pageContext.request.contextPath}${result.profile.sex == '男' ? "/front/img/m25.gif":"/front/img/f25.gif"}>
                </div>
                <!--浮动信息层1-->
                <div class="yhzlmk1" style="position:absolute; left:180px; top:120px;">
                    <div class="yhhxzl" ng-click="dtp.userDimensionInfo.event.clickInfoModel('preferWords')">
                        <div class="p010">
                            <div class="kolhxbt clearfix">我最爱说</div>
                            <ul>
                                <c:forEach items="${result.data.preferWordsInfo}" var="word">
                                    <li>${word}</li>
                                </c:forEach>
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
                            <div class="kolhxbt clearfix"><a class="kolhxgd"></a>相同地区</div>
                            <c:set var="cutAreaData" value="${result.data.araeDatas.cutData}"/>
                            <p>数量：<span class="colorcc3366" >${cutAreaData.areaCount}人</span>
                                <i></i><i>|</i><i></i>集中在：<span class="colorcc3366">${cutAreaData.gather}</span>
                            </p>
                            <p>更多为：<span class="colorcc3366">${cutAreaData.mostGender}性</span>
                            </p>
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
                                <span class="colorcc3366 ng-binding" >
                                    <c:forEach items="${result.data.myInterestsInfo}" var="word">
                                        ${word.text}${" "}
                                    </c:forEach>
                                </span>
                            </p>
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
                            <li>粉丝：<b  class="ng-binding">${result.profile.fans}</b></li>
                            <li>关注：<b  class="ng-binding">${result.profile.focus}</b></li>
                            <li>微博：<b  class="ng-binding">${result.profile.blogNumber}</b></li>
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
                            <td class="yhzllbr"><b  class="ng-binding">${result.profile.address}</b></td>
                        </tr>
                        <tr>
                            <td class="yhzllbl" valign="top">会员等级：</td>
                            <td class="yhzllbr ng-binding" >
                                ${result.profile.member > 0 ? '会员V' : '普通用户'}
                                ${result.profile.member > 0 ? result.profile.member : ''}
                            </td>
                        </tr>
                        <tr>
                            <td class="yhzllbl">个人签名：</td>
                            <td class="yhzllbr ng-binding" >${result.profile.intro}</td>
                        </tr>
                        <tr>
                            <td class="yhzllbl" style="vertical-align: top;">信用级别：</td>
                            <td class="yhzllbr ng-binding" >${result.profile.credit}</td>
                        </tr>
                        <c:if test="${ not empty result.profile.school }">
                            <tr>
                                <td class="yhzllbl" style="vertical-align: top;">学校：</td>
                                <td class="yhzllbr ng-binding" >${result.profile.school}</td>
                            </tr>
                        </c:if>
                        <c:if test="${ not empty result.profile.company }">
                            <tr>
                                <td class="yhzllbl" style="vertical-align: top;">公司：</td>
                                <td class="yhzllbr ng-binding" >${result.profile.company}</td>
                            </tr>
                        </c:if>
                        </tbody></table>
                </div>
            </div>
        </div>
        <div class="charts">
            <%--我最爱说--%>
            <div class="box mb20 p020" id="preferWords" style="display: block;">
                <a class="close" ng-click="dtp.userDimensionChart.event.clickRemoveBtn('preferWords')"></a>

                <div class="tit nobd clearfix">
                    <h2 class="tit-name">我最爱说</h2>
                </div>
                <div class="clearfix">
                    <div class="w460 fl">
                        <div class="box3 p020 height_516">
                            <div class="tit clearfix">
                                <h2 class="tit-name">TOP 5</h2>
                                <ul class="tit-btn clearfix">
                                    <li>
                                        <a id="top5-help" class="help btn-help ng-isolate-scope" title="我最爱说TOP5">
                                            <div class="top5-help-tip">被分析用户近期的微博（约200条），分别按发布时间、评论数、转发数、点赞数排序，图表显示TOP5微博</div>
                                        </a>
                                    </li>
                                </ul>
                            </div>
                            <div class="wastop div_wzas">
                                <ul class="tit-tab clearfix mb10">
                                    <li><a id="top-1" onclick="changeTab(1)" class="current">最新</a>
                                    </li>
                                    <li><a id="top-2" onclick="changeTab(2)">评论最多</a>
                                    </li>
                                    <li><a id="top-3" onclick="changeTab(3)">转发最多</a>
                                    </li>
                                    <li><a id="top-4" onclick="changeTab(4)">点赞最多</a>
                                    </li>
                                </ul>
                                <table id="top-table-1" width="100%" border="0" cellspacing="0" cellpadding="0" class="latest_table top-table">
                                    <tbody>
                                        <c:forEach items="${result.data.top5.latest}" var="blog">
                                            <tr>
                                                <td class="wzasl" title="${blog.date}" width="100px" >${fn:substring(blog.date, 0, 10)}</td>
                                                <td class="wzasc">
                                                    <a target="_blank" title="${blog.postText}">${blog.postCutText}</a>
                                                </td>
                                                <td><a class="wzasxx" target="_blank" title="评论并转发" href="https://m.weibo.cn/compose/repost?id="${blog.id}></a></td>
                                            </tr>
                                        </c:forEach>
                                    </tbody></table>
                                <table class="top-table" id="top-table-2" width="100%" border="0" cellspacing="0" cellpadding="0" >
                                    <tbody>
                                        <c:forEach items="${result.data.top5.mostComments}" var="blog">
                                            <tr>
                                                <td class="wzasl">
                                                    <h2  class="ng-binding">${blog.commentsSize}</h2>
                                                    <span>次评论</span>
                                                </td>
                                                <td class="wzasc">
                                                    <a target="_blank" title="${blog.postText}" >${blog.postCutText}</a>
                                                </td>
                                                <td><a class="wzasxx" title="评论并转发" target="_blank" href="https://m.weibo.cn/compose/repost?id="${blog.id}></a></td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                                <table class="top-table" id="top-table-3" width="100%" border="0" cellspacing="0" cellpadding="0">
                                    <tbody>
                                    <c:forEach items="${result.data.top5.mostForwards}" var="blog">
                                        <tr>
                                            <td class="wzasl">
                                                <h2 class="ng-binding">${blog.forwardSize}</h2>
                                                <span>次转发</span>
                                            </td>
                                            <td class="wzasc">
                                                <a target="_blank" title="${blog.postText}">${blog.postCutText}</a>
                                            </td>
                                            <td>
                                                <a class="wzasxx" title="评论并转发" target="_blank" href="https://m.weibo.cn/compose/repost?id="${blog.id}></a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                                <table class="top-table" id="top-table-4" width="100%" border="0" cellspacing="0" cellpadding="0">
                                    <tbody>
                                    <c:forEach items="${result.data.top5.mostLikes}" var="blog">
                                        <tr>
                                            <td class="wzasl">
                                                <h2 class="ng-binding">${blog.likeSize}</h2>
                                                <span>次点赞</span>
                                            </td>
                                            <td class="wzasc">
                                                <a target="_blank" title="${blog.postText}">${blog.postCutText}</a>
                                            </td>
                                            <td><a class="wzasxx" title="评论并转发" target="_blank"
                                                   href="https://m.weibo.cn/compose/repost?id="${blog.id}></a>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                    <div class="w680 fr">
                        <div class="box4">
                            <div class="clearfix">
                                <ul class="sqhx-xxk clearfix">
                                    <li ng-class="{current:dtp.userDimensionChart.preferWords.wordCloud.active=='weiBoWord'}" name="post_view" ng-click="dtp.userDimensionChart.preferWords.wordCloud.event.clickCloudTab('weiBoWord')" class="current"><a>高频词云</a></li>
                                    </li>
                                </ul>
                                <ul class="tit-btn clearfix">
                                    <li><a class="help btn-help ng-isolate-scope" ng-click="showBangzhuwendangModel()" title="高频词云" content="被分析用户最近90/30天的微博，统计其中每个词出现的频次，频次越大，词显示越大，表示该用户在自己所发的微博中，更喜欢提到这个词。"></a></li>
                                </ul>
                            </div>
                            <div class="tit_box box6 pb40 height_456">
                                <div class="wastop">
                                    <div class="tab_box p020">
                                        <p class="tab_box_list weibo_map" id="wordCloud" style="width:630px;height:456px"><svg width="630" height="456"><g transform="translate(315,228)"><text text-anchor="middle" transform="translate(-108,-113)rotate(90)" style="font-size: 80px; font-family: 微软雅黑; fill: rgb(31, 119, 180);">转发</text><text text-anchor="middle" transform="translate(-67,14)rotate(90)" style="font-size: 10px; font-family: 微软雅黑; fill: rgb(174, 199, 232);">谁看</text><text text-anchor="middle" transform="translate(-13,39)rotate(90)" style="font-size: 10px; font-family: 微软雅黑; fill: rgb(255, 127, 14);">可能</text><text text-anchor="middle" transform="translate(18,41)rotate(90)" style="font-size: 10px; font-family: 微软雅黑; fill: rgb(255, 187, 120);">见了</text><text text-anchor="middle" transform="translate(-59,66)rotate(0)" style="font-size: 10px; font-family: 微软雅黑; fill: rgb(44, 160, 44);">成功</text><text text-anchor="middle" transform="translate(-90,46)rotate(0)" style="font-size: 10px; font-family: 微软雅黑; fill: rgb(152, 223, 138);">r2wizqe</text><text text-anchor="middle" transform="translate(64,-87)rotate(0)" style="font-size: 10px; font-family: 微软雅黑; fill: rgb(214, 39, 40);">双眼皮</text><text text-anchor="middle" transform="translate(92,44)rotate(90)" style="font-size: 10px; font-family: 微软雅黑; fill: rgb(255, 152, 150);">变成</text><text text-anchor="middle" transform="translate(97,-103)rotate(0)" style="font-size: 10px; font-family: 微软雅黑; fill: rgb(148, 103, 189);">rx1o9mp</text><text text-anchor="middle" transform="translate(-86,-106)rotate(90)" style="font-size: 10px; font-family: 微软雅黑; fill: rgb(197, 176, 213);">翰林</text><text text-anchor="middle" transform="translate(-26,-85)rotate(0)" style="font-size: 10px; font-family: 微软雅黑; fill: rgb(140, 86, 75);">大家</text><text text-anchor="middle" transform="translate(-43,84)rotate(0)" style="font-size: 10px; font-family: 微软雅黑; fill: rgb(196, 156, 148);">不行</text><text text-anchor="middle" transform="translate(-157,78)rotate(0)" style="font-size: 10px; font-family: 微软雅黑; fill: rgb(227, 119, 194);">熬夜</text><text text-anchor="middle" transform="translate(-22,-43)rotate(0)" style="font-size: 10px; font-family: 微软雅黑; fill: rgb(247, 182, 210);">美得</text><text text-anchor="middle" transform="translate(143,95)rotate(90)" style="font-size: 10px; font-family: 微软雅黑; fill: rgb(127, 127, 127);">坚持</text></g></svg></p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <p class="p10"></p>
            </div>
        </div>
    </div>
    <%@include file="/inc/bottom.jsp" %>
</div>
<%@include file="/inc/footer.jsp"%>
<script src="${pageContext.request.contextPath}/front/js/detail.js"></script>
<script>
    var result = ${result}
</script>
</body>
</html>
