/**
 * Created by guin_guo on 2018/2/28.
 */
$(function () {
    bindEvent();
    run();
});
Date.prototype.Format = function (fmt) { //author: meizz
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "H+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
};
changeTab = function (tab) {
    $('.tit-tab li a').removeClass('current');
    $('.wastop table').hide();
    $('#top-table-'+tab).show();
    $('.tit-tab li a#top-'+tab).addClass('current');
};
changeCity = function (tab) {
    $('.city_area .tit-tab li a').removeClass('current');
    $('.city_box_list .city-data').hide();
    $('.city_box_list .city-data-'+tab).show();
    $('.city_area .tit-tab li a#tab-area-'+tab).addClass('current');
    var mapData = result.data.areaDatas.citys.city;
    if (tab == 'province') {
        mapData = [{name: result.data.areaDatas.citys.provice, value: result.data.areaDatas.fansNum}];
    }
    try {
        kolEchart.drawMap(kolEchart.getChart("fansArealDistribution"), mapData);
    } catch (e) {
        console.error(e)
    }
};
function bindEvent() {
    $('#top5-help').hover(function(){
        $('#top5-help div.top5-help-tip').show();
    },function(){
        $('#top5-help div.top5-help-tip').hide();
    });
    /*//点击+任务
    $('#newTask_add').on('click', function () {
        $('#xubox_shade1, #xubox_layer2').show();
        searchUser(1,5);
    });
    //监听输入框
    $('#searchInput').on('input propertychange', function () {
        $('#xubox_shade1, #xubox_layer2').show();
        console.log($('#searchInput').val());
        searchUser(1,5,$('#searchInput').val());
    });
    //关闭弹窗
    $('.guanbi').on('click', function() {
        $('#xubox_shade1, #xubox_layer2, #select-user').hide();
    });
    //搜索用户数据
    searchUser = function(num, pageSize, username) {
        if (!username) {
            username = $('#searchInput').val();
        }
        $.ajax({
            type: "GET",
            url: ctx + "/users",
            dataType: "json",
            data:{
                num:num,
                pageSize:pageSize,
                username:username,
            },
            success: function(data) {
                var users = data.datas;
                var html = '';
                users.forEach(function(user, idx, array){
                    var sexClass = (user.sex == '男' ? 'boy_tb' : 'girl_tb');
                    var a = '<li class="clearfix border_dashed" onclick="checkUser(&quot;'+user.id+',' +user.nickname +'&quot;)">' +
                                '<div class="fang_user_list"> ' +
                                    '<p class="user_list_weixin clearfix">' +
                                        '<span class="header_02">' +
                                            '<img width="40" height="40" src="'+user.avatar+'">' +
                                        '</span>' +
                                    '</p> ' +
                                    '<i style="height: 16px;">' + user.nickname;
                                    if(user.member>=5) {
                                        a += '<img src='+ctx+'/front/img/hv.png style="height: 16px; width: 16px;">';
                                    } else if (user.level>=24) {
                                        a += '<img src='+ctx+'/front/img/lv.png style="height: 16px; width: 16px;">';
                                    }
                                    a += '</i><br><div class="clearfix bean_vermicelli"><b class="'+sexClass+'"></b>' +
                                         '<em>粉丝数：'+user.fans+'</em>' +
                                         '<em class="user_em">微博数：'+user.blogNumber+'</em>' +
                                         '<em class="user_em">关注量：'+user.focus+'</em>' +
                                        '<em class="user_em">'+user.address+'</em>' +
                                    '</div> ' +
                                '</div>' +
                            '</li>';
                    html+=a;
                })
                // $("#work-experience-table>tr:first-child").nextAll().remove();
                $('#user-data').html(html);
                html = '';
                var lastPage = data.total % data.pageSize == 0 ? data.total / data.pageSize : parseInt(data.total / data.pageSize) + 1;
                if(data.num < lastPage) {
                    html += '<a class="fr pg_next small_button" onclick="searchUser('+(data.num+1)+')">下一页</a>';
                }
                if(data.num > 1) {
                    html += '<a class="fr pg_last mr10 small_button" onclick="searchUser('+(data.num-1)+')">上一页</a>'
                }
                $('#search_page').html(html);
            },
            error:function(data) {
                alert("获取用户数据失败！");
            }
        });
    }*/
}
function run() {
    // var result = result;
    //画词云
    kolEchart.drawWordCloud("wordCloud", result.data.wordCloud);
    //画兴趣雷达
    dradMyInterests();
    //画地图
    changeCity('province');
    //画博龄
    drawBlogAge();
    //画学校排行
    drawSchoolRank();
    //画会员等级
    drawMember();
    //画公司标签词云
    drawCompanyTag();
    //画等级分布
    drawLevel();
    //画粉丝区间
    drawFansRank();
}
/**
 * 画兴趣图
 */
function dradMyInterests() {
    //加载数据
    var myInterests = result.data.userLabels;
    //显示图表
    try {
        kolEchart.drawRadar(kolEchart.getChart("interest"), formatData.formatInterest(myInterests));
    } catch (e) {
        console.error(e)
    }
}
/**
 * 博龄
 */
function drawBlogAge() {
    var blogAges = result.data.areaDatas.blogAges;
    var blogAgesData = {};
    for (var i=0;i<blogAges.length;i++) {
        var age = blogAges[i];
        blogAgesData[age.key + '年'] = age.value;
    }
    try {
        kolEchart.drawAgePie(kolEchart.getChart("age"), formatData.formatAgeData(blogAgesData));
    } catch (e) {
        console.error(e)
    }
}
/**
 * 学校
 */
function drawSchoolRank() {
    var schoolRank = result.data.areaDatas.schoolRank;
    var schoolRankData = {};
    for (var i=0;i<schoolRank.length;i++) {
        var age = schoolRank[i];
        schoolRankData[age.key] = age.value;
    }
    try {
        kolEchart.drawBar1(kolEchart.getChart("school_rank"), formatData.formatSchoolData(schoolRankData));
    } catch (e) {
        console.error(e)
    }
}
/**
 * 会员
 */
function drawMember() {
    var memberLevel = result.data.areaDatas.memberLevels;
    var memberLevelData = {};
    for (var i=0;i<memberLevel.length;i++) {
        var member = memberLevel[i];
        if (member.key == 0) {
            memberLevelData['非会员'] = member.value;
        } else {
            memberLevelData[member.key + '级会员'] = member.value;
        }
    }
    try {
        kolEchart.drawAgePie(kolEchart.getChart("user_type"), formatData.formatAgeData(memberLevelData));
    } catch (e) {
        console.error(e)
    }
}
/**
 * 公司标签
 */
function drawCompanyTag() {
    try {
        kolEchart.drawWordCloud("jobTag", result.data.areaDatas.companyTag);
    } catch (e) {
        console.error(e)
    }
}
/**
 * 等级
 */
function drawLevel() {
    try { //格式化图表
        var data = formatData.formatLevelData(result.data.areaDatas.levels);
        //画图
        kolEchart.drawBar(kolEchart.getChart("level"), data.lvchartData, data.xData);
    } catch (e) {
        console.error(e)
    }
}
/**
 * 粉丝区间
 */
function drawFansRank() {
    var fansRank = result.data.areaDatas.fansRange;
    var fansRankData = {};
    for (var i=0;i<fansRank.length;i++) {
        var fans = fansRank[i];
        fansRankData[fans.key] = fans.value;
    }
    try {
        kolEchart.drawBar1(kolEchart.getChart("fans_range"), formatData.formatFansRangeData(fansRankData));
    } catch (e) {
        console.error(e)
    }
}