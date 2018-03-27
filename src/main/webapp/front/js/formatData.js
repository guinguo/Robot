/**
 * @Discription bvdatac
 * @Author      zhangcong
 * @CreateDate  2016/3/3  10:05
 * @Version     1.0
 */
var formatData = {
    /**
     * 格式化等级分布数据
     * param:flevel(原数据)
     * */
    formatLevelData: function (flevel) {

        //数据对象
        var lvchartData = {};
        lvchartData.series = [];
        var ldata = [];
        //X轴数组
        var xData = [];
        var total = 0;
        lvchartData.max = 0;
        var fansLength = flevel.length;
        for (var i = fansLength - 1; i >= 0; i--) {
            //替换掉级
            //flevel[i].level = flevel[i].level.replace("级", "");
            //计算总数
            total = total + flevel[i].count;
            if (flevel[i].count > lvchartData.max) {
                lvchartData.max = flevel[i].count;
            }
            ldata[i] = flevel[i].count;
            xData[i] = flevel[i].level;
        }
        lvchartData.max = lvchartData.max + (lvchartData.max / 10 * 4);
        for (var i = 0; i < ldata.length; i++) {
            var value = Math.round((ldata[i] / total) * 1000) / 10;
            if (value == 0) ldata[i] = 0;
            lvchartData.series[i] = {value: value, xAxis: i, yAxis: ldata[i] + 1};
        }
        lvchartData.data = ldata;
        var returnData = {};
        returnData.lvchartData = lvchartData;
        returnData.xData = xData;
        return returnData;
    },
    /**
     * 格式化年龄分布数据
     * param:ageData(原数据)
     * */
    formatAgeData: function (ageData) {
        var legendData = [];
        var data = [];
        for (var param in ageData) {
            //if (param == '其它') continue;
            legendData.push(param);
            var tempObj = {};
            tempObj.value = ageData[param];
            tempObj.name = param;
            data.push(tempObj);
        }
        var returnData = {};
        returnData.legendData = legendData;
        returnData.data = data;
        return returnData;
    },
    /**
     * 格式化学校排名数据
     * param:schoolData(原数据)
     * */
    formatSchoolData: function (schoolData) {
        var count = 0;
        var schoolName = [];
        var data = [];
        var tempArr = [];

        for (var param in schoolData) {
            var tempObj = {};
            tempObj.name = getSchool(param);
            tempObj.value = schoolData[param];
            tempArr.push(tempObj);
        }

        tempArr = tempArr.sort(compare('value'));

        var index = tempArr.length >= 9 ? 9 : tempArr.length-1;
        for (var j = index; j >= 0; j--) {
            schoolName.push(tempArr[j].name);
            data.push(tempArr[j].value);
            count = count + tempArr[j].value;
        }
        var returnData = {};
        returnData.data = data;
        returnData.yAxis = schoolName;
        return returnData;
    },
    /**
     * 格式化学校排名数据
     * param:schoolData(原数据)
     * */
    formatFansRangeData: function (schoolData) {
        var count = 0;
        var schoolName = [];
        var data = [];
        var tempArr = [];

        for (var param in schoolData) {
            var tempObj = {};
            tempObj.name = getSchool(param);
            tempObj.value = schoolData[param];
            tempArr.push(tempObj);
        }


        var index = tempArr.length >= 9 ? 9 : tempArr.length-1;
        for (var j = index; j >= 0; j--) {
            schoolName.push(tempArr[j].name);
            data.push(tempArr[j].value);
            count = count + tempArr[j].value;
        }
        var returnData = {};
        returnData.data = data;
        returnData.yAxis = schoolName;
        return returnData;
    },
    /**
     * 格式化兴趣雷达图数据
     * param:interestData(原数据)
     * */
    formatInterest: function (interestData) {
        var chartData = {};
        var text = [];
        var series = [];
        var max = 1;
        for (var i = 0; i < interestData.length; i++) {
            if (interestData[i].score > max) {
                max = parseInt(interestData[i].score);
            }
        }
        for (var i = 0; i < interestData.length; i++) {
            if (i < 10) {
                text.push({text: interestData[i].text, max: max})
                series.push(interestData[i].score);
            }
            if (i > 10) {
                break;
            }
        }

        //初始化图表节点数据
        chartData.indicator = text;
        chartData.series = series;
        return chartData;
    },
    /**
     * 格式化星座分布数据
     * param:constellationData(原数据)
     * */
    formatConstellation: function (constellationData) {
        var other = 0;
        var count = 0;
        var temp = {"白羊": 0, "金牛": 0, "双子": 0, "巨蟹": 0, "狮子": 0, "处女": 0, "天秤": 0, "天蝎": 0, "射手": 0, "魔羯": 0, "水瓶": 0, "双鱼": 0};
        var jsonData = constellationData;
        for (var param in jsonData) {
            if (param == '其它') {
                other = jsonData[param];
                continue;
            }
            count = count + jsonData[param];
            temp[param] = jsonData[param];
        }
        var data = [];
        var xData = [];
        for (var param in temp) {
            data.push(temp[param]);
            xData.push(param);
        }
        var markDatas = [];
        for (var i = 0; i < 12; i++) {
            var markData = {};
            markData.xAxis = i;
            markData.y = 190;
            markData.symbolSize = 10;
            markData.symbol = context+'/resources/images/constellation-' + (i + 1) + '.png';
            markDatas.push(markData);
        }
        var returnData = {};
        returnData.xData = xData;
        returnData.markData = markDatas;
        returnData.data = data;
        return returnData;
    }
}

/**
 * 去除学校上面的（2015年）这种信息
 * 只保留学校这样子
 * @param str
 * @returns {XML|string|void|*}
 */
function getSchool(str) {
    str = str.replace(/\(.*?\)/g, '');
    var strLength = str.length;
    if(strLength>=14) {
        str = str.substring(0, 12)+"...";
    }
    return str;
}

/**
 * 数组里面的对象比较
 * eg: terrArr.sort(compare('count')) //count：要排序的字段 要排序的数组，里面放的是obj
 * @param propertyName 要比较的数组对象
 * @returns {Function}
 */
function compare(propertyName) {
    return function (object1, object2) {
        var value1 = object1[propertyName];
        var value2 = object2[propertyName];
        if (value2 < value1) {
            return -1;
        }
        else if (value2 > value1) {
            return 1;
        }
        else {
            return 0;
        }
    }
}