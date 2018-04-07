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
                    <div class="yhhxzl">
                        <div class="p010">
                            <div class="kolhxbt clearfix">我最爱说</div>
                            <ul>
                                <c:forEach items="${result.data.preferWordsInfo}" var="word">
                                    <li>${word.text}</li>
                                </c:forEach>
                            </ul>
                            <p><a class="kolhxgd"></a></p>
                        </div>
                    </div>
                    <div class="dian"><img src=${pageContext.request.contextPath}/front/img/dian.png width="40" height="40" alt=""></div>
                </div>
                <!--浮动信息层2-->
                <div class="yhzlmk3" style="position:absolute; left:80px; top:350px;">
                    <div class="yhhxzl">
                        <div class="p010">
                            <div class="kolhxbt clearfix"><a class="kolhxgd"></a>相同地区</div>
                            <c:set var="cutAreaData" value="${result.data.areaDatas.cutData}"/>
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
                    <div class="yhhxzl">
                        <div class="p010">
                            <div class="kolhxbt clearfix"><a class="kolhxgd"></a>我的兴趣</div>
                            <p>
                                <span class="colorcc3366" >
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
                            <li>粉丝：<b >${result.profile.fans}</b></li>
                            <li>关注：<b >${result.profile.focus}</b></li>
                            <li>微博：<b >${result.profile.blogNumber}</b></li>
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
                            <td class="yhzllbr"><b >${result.profile.address}</b></td>
                        </tr>
                        <tr>
                            <td class="yhzllbl" valign="top">会员等级：</td>
                            <td class="yhzllbr" >
                                ${result.profile.member > 0 ? '会员V' : '普通用户'}
                                ${result.profile.member > 0 ? result.profile.member : ''}
                            </td>
                        </tr>
                        <tr>
                            <td class="yhzllbl">个人签名：</td>
                            <td class="yhzllbr" >${result.profile.intro}</td>
                        </tr>
                        <tr>
                            <td class="yhzllbl" style="vertical-align: top;">信用级别：</td>
                            <td class="yhzllbr" >${result.profile.credit}</td>
                        </tr>
                        <c:if test="${ not empty result.profile.school }">
                            <tr>
                                <td class="yhzllbl" style="vertical-align: top;">学校：</td>
                                <td class="yhzllbr" >${result.profile.school}</td>
                            </tr>
                        </c:if>
                        <c:if test="${ not empty result.profile.company }">
                            <tr>
                                <td class="yhzllbl" style="vertical-align: top;">公司：</td>
                                <td class="yhzllbr" >${result.profile.company}</td>
                            </tr>
                        </c:if>
                        </tbody></table>
                </div>
            </div>
        </div>
        <div class="charts">
            <%--我最爱说--%>
            <div class="box mb20 p020" id="preferWords" style="display: block;">
                <%--<a class="close"></a>--%>
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
                                        <a id="top5-help" class="help btn-help" title="我最爱说TOP5">
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
                                                <td><a class="wzasxx" target="_blank" title="评论并转发" href="https://m.weibo.cn/compose/repost?id=${blog.id}"></a></td>
                                            </tr>
                                        </c:forEach>
                                    </tbody></table>
                                <table class="top-table" id="top-table-2" width="100%" border="0" cellspacing="0" cellpadding="0" >
                                    <tbody>
                                        <c:forEach items="${result.data.top5.mostComments}" var="blog">
                                            <tr>
                                                <td class="wzasl">
                                                    <h2 >${blog.commentsSize}</h2>
                                                    <span>次评论</span>
                                                </td>
                                                <td class="wzasc">
                                                    <a target="_blank" title="${blog.postText}" >${blog.postCutText}</a>
                                                </td>
                                                <td><a class="wzasxx" title="评论并转发" target="_blank" href="https://m.weibo.cn/compose/repost?id=${blog.id}"></a></td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                                <table class="top-table" id="top-table-3" width="100%" border="0" cellspacing="0" cellpadding="0">
                                    <tbody>
                                    <c:forEach items="${result.data.top5.mostForwards}" var="blog">
                                        <tr>
                                            <td class="wzasl">
                                                <h2>${blog.forwardSize}</h2>
                                                <span>次转发</span>
                                            </td>
                                            <td class="wzasc">
                                                <a target="_blank" title="${blog.postText}">${blog.postCutText}</a>
                                            </td>
                                            <td>
                                                <a class="wzasxx" title="评论并转发" target="_blank" href="https://m.weibo.cn/compose/repost?id=${blog.id}"></a>
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
                                                <h2>${blog.likeSize}</h2>
                                                <span>次点赞</span>
                                            </td>
                                            <td class="wzasc">
                                                <a target="_blank" title="${blog.postText}">${blog.postCutText}</a>
                                            </td>
                                            <td><a class="wzasxx" title="评论并转发" target="_blank"
                                                   href="https://m.weibo.cn/compose/repost?id=${blog.id}"></a>
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
                                    <li name="post_view" class="current"><a>高频词云</a></li>
                                    </li>
                                </ul>
                                <ul class="tit-btn clearfix">
                                    <li><a class="help btn-help" title="高频词云" content="被分析用户最近90/30天的微博，统计其中每个词出现的频次，频次越大，词显示越大，表示该用户在自己所发的微博中，更喜欢提到这个词。"></a></li>
                                </ul>
                            </div>
                            <div class="tit_box box6 pb40 height_456">
                                <div class="wastop">
                                    <div class="tab_box p020">
                                        <p class="tab_box_list weibo_map" id="wordCloud" style="width:630px;height:456px">
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <p class="p10"></p>
            </div>
            <%--我的兴趣--%>
            <div class="box mb20 p020" id="myInterests" style="display: block;">
                <%--<a class="close"></a>--%>
                <div class="tit nobd clearfix">
                    <h2 class="tit-name">我的兴趣</h2>
                    <ul class="tit-btn clearfix">
                        <li>
                            <a class="help btn-help"
                               title="我的兴趣" content="被分析用户的兴趣是通过TA的关注关系，计算出TA在每个兴趣标签的分值，分值越高，表示该用户更倾向于有这个兴趣。"></a>
                        </li>
                    </ul>
                </div>
                <div class="box3 clearfix height_415 div_wdxq">
                    <div class="w340 fl">
                        <div class="p020">
                            <table class="wdxq" width="100%" border="0" cellspacing="0" cellpadding="0">
                                <!-- <span class="wdxqjt"></span> -->
                                <tbody>
                                <tr class="wdxqtit">
                                    <td class="wdxql">兴趣标签</td>
                                    <td class="wdxqc">分数</td>
                                    <td class="wdxqr"><!-- <a class="help"></a> --></td>
                                </tr>
                                <c:forEach items="${result.data.userLabels}" var="word">
                                    <tr>
                                        <td class="wdxql">${word.text}</td>
                                        <td class="wdxqc"><b>${word.score}</b></td>
                                        <td class="wdxqr"></td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <div class="w800 blue_map fr p20" style="text-align:center;">
                        <p id="interest"
                           style="width: 800px; height: 350px; -webkit-tap-highlight-color: transparent; user-select: none; background-color: rgba(0, 0, 0, 0); cursor: default;"
                           _echarts_instance_="1523016051766">
                        </p>
                    </div>
                </div>
                <p class="p10"></p>
            </div>
            <%--相同地区--%>
            <div class="box mb20 p020 " id="myFans" style="display: block;">
                <div class="tit nobd clearfix">
                    <h2 class="tit-name">相同地区</h2>
                    <span class="pink small_title_line">${result.data.areaDatas.fansNum}</span>
                    <span class="grey6">个用户&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;通过匹配爬取的用户库获得的数据。</span>
                </div>
                <div class="clearfix">
                    <%--第一个：男女比例+地域分布--%>
                    <div class="clearfix">
                        <div class="box3 con_col p020 mb20 fl height_360">
                            <div class="tit nobd clearfix">
                                <h2 class="tit-name">男女比例</h2>
                                <ul class="tit-btn clearfix">
                                    <li><a class="help btn-help" title="男女比例" content="基于新浪微博公开的信息，统计与该用户相同地区的性别分布。"></a></li>
                                </ul>
                            </div>
                            <div class="xbfb div_xbfb">
                                <ul class="clearfix">
                                    <li class="nan">
                                        <div><img src="${pageContext.request.contextPath}/front/img/nan.png"></div>
                                        <h2>${result.data.areaDatas.sexRadio.man.radio}</h2>

                                        <p>男：<b>${result.data.areaDatas.sexRadio.man.size}</b>人</p>
                                    </li>
                                    <li class="nv">
                                        <div><img src="${pageContext.request.contextPath}/front/img/nv.png"></div>
                                        <h2>${result.data.areaDatas.sexRadio.woman.radio}</h2>

                                        <p>女：<b>${result.data.areaDatas.sexRadio.woman.size}</b>人</p>
                                    </li>
                                    <li class="wz">
                                        <div><img src="${pageContext.request.contextPath}/front/img/wz.png"></div>
                                        <h2>0.0%</h2>

                                        <p>未知：<b>0</b>人</p>
                                    </li>
                                </ul>
                            </div>
                        </div>
                        <div class="box3 con_col p020 mb20 fr height_360 city_area">
                            <div class="tit nobd clearfix">
                                <h2 class="tit-name">地域分布</h2>
                                <ul class="tab_title tit-tab clearfix fl">
                                    <li><a id="tab-area-province" onclick="changeCity('province')" class="current">省份</a>
                                    </li>
                                    <li><a id="tab-area-city" onclick="changeCity('city')">城市</a>
                                    </li>
                                </ul>
                                <ul class="tit-btn clearfix fr">
                                    <li><a class="help btn-help" title="地域分布" content="基于用户在新浪微博公开的信息，统计该KOL粉丝所在省份及城市分布。"></a></li>
                                </ul>
                            </div>
                            <div class="clearfix">
                                <div class="wastop div_dyfb">
                                    <div class="tab_box p020 clearfix">
                                        <div class="tab_box_list city_box_list">

                                            <div class="dyfb city-data city-data-province">

                                                <dl class="clearfix">
                                                    <dt>${result.data.areaDatas.citys.provice}</dt>
                                                </dl>
                                                <ul class="clearfix">
                                                    <li>${result.data.areaDatas.fansNum}人</li>
                                                </ul>
                                            </div>

                                            <div class="dyfb city-data city-data-city" style="display: none">


                                                <c:forEach items="${result.data.areaDatas.citys.city}" begin="0" end="4" var="city">
                                                    <div>
                                                        <dl class="clearfix">
                                                            <dt>${city.name}</dt>
                                                        </dl>
                                                        <ul class="clearfix">
                                                            <li>${city.value}人</li>
                                                        </ul>
                                                    </div>
                                                </c:forEach>
                                            </div>

                                            <div class="distribution_map">
                                                <div style="width: 350px; height: 230px; -webkit-tap-highlight-color: transparent; user-select: none; background-color: rgba(0, 0, 0, 0);" id="fansArealDistribution" _echarts_instance_="1523016051759"></div>

                                            </div>

                                        </div>

                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <%--第二个：博龄分布+学校排名--%>
                    <div class="clearfix hide_new_chart">
                        <div class="box3 con_col p020 mb20 fl height_330">
                            <div class="tit nobd clearfix">
                                <h2 class="tit-name">博龄分布</h2>
                                <ul class="tit-btn clearfix">
                                    <li><a class="help btn-help" title="年龄分布" content="基于用户在新浪微博公开的信息，统计该KOL粉丝的年龄分布。"></a></li>
                                </ul>
                            </div>
                            <div class="wastop div_nlfb">
                                <p class="col_map" style="width: 445px; height: 250px; cursor: default; background-color: rgba(0, 0, 0, 0); -webkit-tap-highlight-color: transparent; user-select: none;" id="age" _echarts_instance_="1523016051760"></p>
                            </div>
                        </div>
                        <div class="box3 con_col p020 mb20 fr height_330 hide_new_chart">
                            <div class="tit nobd clearfix">
                                <h2 class="tit-name">学校排名</h2>
                                <ul class="tit-btn clearfix">
                                    <li><a class="help btn-help" title="学校排名" content="基于用户在新浪微博公开的信息，统计该KOL粉丝的学校排名。"></a></li>
                                </ul>
                            </div>

                            <div class="wastop div_xxpm">

                                <div class="col_map" style="width: 445px; height: 250px; cursor: default; background-color: rgba(0, 0, 0, 0); -webkit-tap-highlight-color: transparent; user-select: none;" id="school_rank" _echarts_instance_="1523016051762"></div>
                            </div>

                        </div>
                    </div>
                    <%--第三个：微博会员+公司标签--%>
                    <div class="clearfix">
                        <div class="box3 con_col p020 mb20 fl height_330">
                            <div class="tit nobd clearfix">
                                <h2 class="tit-name">微博会员</h2>
                                <ul class="tit-btn clearfix">
                                    <li><a class="help btn-help" title="微博会员" content="每个新浪微博的用户都对应一个微博身份，包括普通身份、微博达人、个人认证（黄V）、企业认证（蓝V）、微博女郎等，图表统计的是该KOL粉丝的身份分布。"></a></li>
                                </ul>
                            </div>
                            <div class="wastop div_wbsf">
                                <p class="col_map" style="width: 445px; height: 230px; cursor: default; background-color: rgba(0, 0, 0, 0); -webkit-tap-highlight-color: transparent; user-select: none;" id="user_type" _echarts_instance_="1523016051763"></p>
                            </div>
                        </div>
                        <div class="box3 con_col p020 mb20 fr height_330 hide_new_chart">
                            <div class="tit nobd clearfix">
                                <h2 class="tit-name">公司标签</h2>
                                <ul class="tit-btn clearfix fr">
                                    <li><a class="help btn-help" title="职业标签" content="基于用户在新浪微博公开的信息，统计该KOL粉丝的职业标签。"></a></li>
                                </ul>
                            </div>
                            <div class="clearfix">
                                <div class="wastop">
                                    <div class="tab_box p020">
                                        <p class="tab_box_list weibo_map" id="jobTag" style="width:445px;height:250px;"></p>
                                    </div>
                                </div>

                            </div>
                        </div>
                    </div>
                    <%--第四个：等级分布+粉丝区间--%>
                    <div class="clearfix">
                        <div class="box3 con_col p020 mb20 fl height_330 old_djfb_width">
                            <div class="tit nobd clearfix">
                                <h2 class="tit-name">等级分布</h2>
                                <ul class="tit-btn clearfix">
                                    <li><a class="help btn-help" title="等级分布" content="基于用户在新浪微博的等级，统计该KOL粉丝的等级分布区间。"></a></li>
                                </ul>
                            </div>
                            <div class="wastop div_djfb">
                                <div class="col_map" style="width: 445px; height: 250px; cursor: default; background-color: rgba(0, 0, 0, 0); -webkit-tap-highlight-color: transparent; user-select: none;" id="level" _echarts_instance_="1523016051764"></div>

                            </div>
                        </div>
                        <div class="box3 con_col p020 mb20 fr height_330 hide_new_chart">
                            <div class="tit nobd clearfix">
                                <h2 class="tit-name">粉丝区间</h2>
                                <ul class="tit-btn clearfix fr">
                                    <li>
                                        <a class="help btn-help" title="粉丝区间" content="基于BlueMC Data的数据，统计该KOL粉丝的粉丝分布区间"></a>
                                    </li>
                                </ul>
                            </div>
                            <div class="clearfix">
                                <div class="wastop div_fsfb">

                                    <div class="col_map" style="width: 445px; height: 250px; cursor: default; background-color: rgba(0, 0, 0, 0); -webkit-tap-highlight-color: transparent; user-select: none;" id="fans_range" _echarts_instance_="1523016051765"></div></div>
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
<script src="${pageContext.request.contextPath}/front/js/d3/d3.min.js"></script>
<script src="${pageContext.request.contextPath}/front/js/echarts-all.js"></script>
<script src="${pageContext.request.contextPath}/front/js/d3/d3.layout.cloud.js"></script>
<script src="${pageContext.request.contextPath}/front/js/kolEcharts.js"></script>
<script src="${pageContext.request.contextPath}/front/js/formatData.js"></script>
<script src="${pageContext.request.contextPath}/front/js/detail.js"></script>
<script>
    var result = ${result}
</script>
</body>
</html>
