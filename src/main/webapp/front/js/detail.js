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
    bindHelpEvent();
}
function bindHelpEvent() {
    $('#top5-help').hover(function(){
        $('#top5-help div.top5-help-tip').show();
    },function(){
        $('#top5-help div.top5-help-tip').hide();
    });
    
    $('#word-cloud-help').hover(function(){
        $('#word-cloud-help div.word-cloud-help-tip').show();
    },function(){
        $('#word-cloud-help div.word-cloud-help-tip').hide();
    });
    
    $('#my-interest-help').hover(function(){
        $('#my-interest-help div.my-interest-help-tip').show();
    },function(){
        $('#my-interest-help div.my-interest-help-tip').hide();
    });
    
    $('#sex-radio-help').hover(function(){
        $('#sex-radio-help div.sex-radio-help-tip').show();
    },function(){
        $('#sex-radio-help div.sex-radio-help-tip').hide();
    });
    
    $('#citys-help').hover(function(){
        $('#citys-help div.citys-help-tip').show();
    },function(){
        $('#citys-help div.citys-help-tip').hide();
    });
    
    $('#blog-age-help').hover(function(){
        $('#blog-age-help div.blog-age-help-tip').show();
    },function(){
        $('#blog-age-help div.blog-age-help-tip').hide();
    });
    
    $('#school-rank-help').hover(function(){
        $('#school-rank-help div.school-rank-help-tip').show();
    },function(){
        $('#school-rank-help div.school-rank-help-tip').hide();
    });
    
    $('#member-help').hover(function(){
        $('#member-help div.member-help-tip').show();
    },function(){
        $('#member-help div.member-help-tip').hide();
    });
    
    $('#company-help').hover(function(){
        $('#company-help div.company-help-tip').show();
    },function(){
        $('#company-help div.company-help-tip').hide();
    });
    
    $('#level-help').hover(function(){
        $('#level-help div.level-help-tip').show();
    },function(){
        $('#level-help div.level-help-tip').hide();
    });
    
    $('#fans-help').hover(function(){
        $('#fans-help div.fans-help-tip').show();
    },function(){
        $('#fans-help div.fans-help-tip').hide();
    });
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