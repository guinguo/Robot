/**
 * @Discription bvdatac
 * @Author      zhangcong
 * @CreateDate  2016/3/3  9:35
 * @UpdateDate  2016/5/6  14:53
 * @Version     1.0
 */
var colors = [
    '#CB3366', '#FD7F01', '#6495ed',
    '#ff69b4', '#ba55d3', '#cd5c5c', '#ffa500', '#40e0d0',
    '#1e90ff', '#ff6347', '#7b68ee', '#00fa9a', '#ffd700',
    '#6b8e23', '#ff00ff', '#3cb371', '#b8860b', '#30e0e0'
];

var kolEchart = {
    /**
     * 根据id获取echart对象
     * param:echartId(echart元素Id)
     * */
    getChart: function (echartId) {
        return echarts.init(document.getElementById(echartId));
    },
    /**
     * 画地图
     * */
    drawMap: function (mapChart, mapData) {

        if(mapData&&mapData.length) {
            mapData = mapData.sort(function(node1,node2) {
                return node2.value - node1.value;
            })
        }
        //console.log(mapData);
        var mapOption = {
            dataRange: {
                show: true,
                min: 0,
                max: mapData[0].value,
                x: 'right',
                y: 'bottom',
                //itemHeight: 30,
                right: 80,
                text: ['高', '低'],           // 文本，默认为数值文本
                calculable: false,
                splitNumber: 0
            },
            tooltip: {
                trigger: 'item'
            },
            roamController: {
                show: false,
                x: 'right',
                mapTypeControl: {
                    'china': true
                }
            },
            series: [
                {
                    name: '人数',
                    type: 'map',
                    mapType: 'china',
                    roam: false,
                    itemStyle: {
                        /*normal: {label: {show: true}},*/
                        emphasis: {label: {show: true}}
                    },
                    data: mapData
                }
            ]
        };
        mapChart.setOption(mapOption);
    },
    /**
     * 生成柱状图方法
     * param:chart(echart对象),chartData(图表数据),xData(x轴数据)
     * */
    drawBar: function (chart, chartData, xData) {
        var levelOption = {
            xAxis: [
                {
                    type: 'category',
                    data: xData,
                    splitLine: {show: false}
                }
            ],
            yAxis: [
                {
                    type: 'value',
                    axisLine: {show: false},
                    axisLabel: {show: false},
                    splitLine: {show: false},
                    max: chartData.max
                }
            ],
            grid: {
                x: 0,
                y: 5,
                x2: 0,
                y2: '18%',
                borderWidth:0
            },
            series: [
                {
                    type: 'bar',
                    barWidth: 50,
                    markPoint: {
                        itemStyle: {
                            normal: {
                                color: (function () {
                                    var zrColor = zrender.tool.color;
                                    return zrColor.getLinearGradient(
                                        0, 0, 1000, 0,
                                        [
                                            [0, 'rgba(22,198,139,1)'],
                                            [0.5, 'rgba(102,102,204,1)'],
                                            [1, 'rgba(51,153,204, 1)']
                                        ]
                                    )
                                })(),
                                label: {
                                    show: true,
                                    formatter: '{c}%'
                                }
                            }

                        },
                        symbolSize: 18,
                        data: chartData.series
                    },
                    itemStyle: {
                        normal: {
                            color: (function () {
                                var zrColor = zrender.tool.color;
                                return zrColor.getLinearGradient(
                                    0, 0, 1000, 0,
                                    [
                                        [0, 'rgba(22,198,139,1)'],
                                        [0.5, 'rgba(102,102,204,1)'],
                                        [1, 'rgba(51,153,204, 1)']
                                    ]
                                )
                            })(),
                            label: {
                                show: true, position: 'insideLeft'
                            }
                        },
                        emphasis: {
                            color: (function () {
                                var zrColor = zrender.tool.color;
                                return zrColor.getLinearGradient(
                                    0, 0, 1000, 0,
                                    [
                                        [0, 'rgba(22,198,139,1)'],
                                        [0.5, 'rgba(102,102,204,1)'],
                                        [1, 'rgba(51,153,204, 1)']
                                    ]
                                )
                            })(),
                            label: {
                                show: true, position: 'insideLeft'
                            }
                        }
                    },
                    data: chartData.data
                }
            ]
        };
        chart.setOption(levelOption);
    },
    /**
     * 画力导向(g关系)图
     * */
    drawEchartSocail: function (influenceChart, socailData) {
        var nodes = [];
        var nodesTemp = eval(JSON.stringify(socailData.nodes));
        var top5 = new Array();
        //根据center值取出排名前五的
        var sortNodes = socailData.nodes.sort(function (a, b) {
            return b.in - a.in
        });
        for (var i = 0; i < sortNodes.length && i < 5; i++) {
            top5.push(sortNodes[i]);
        }

        for (var param in nodesTemp) {
            var nodeObj = {};
            nodeObj.name = nodesTemp[param].userUrn;
            nodeObj.value = nodesTemp[param].value;
            nodeObj.label = nodesTemp[param].name;
            //排名前五的显示红色
            if (isEquals(top5, nodeObj,"userUrn","name")) {
                nodeObj.itemStyle = {
                    normal: {
                        color: 'red'
                    }
                }
            } else {
                nodeObj.itemStyle = {
                    normal: {
                        color: '#3992FD'
                    }
                }
            }
            nodes.push(nodeObj);
        }
        var option = {
            tooltip: {
                trigger: 'item',
                formatter: function (params, ticket, callback) {
                    if (params[3] == null) {
                        var name = params.data.label;
                        return name;
                    } else {
                        return '关注关系：<br/>' + nodes[params.data.source].label + ' -->' + nodes[params.data.target].label
                    }
                }
            },
            series: [
                {
                    type: 'force',
                    ribbonType: false,
                    categories: [
                        {
                            name: '0'
                        },
                        {
                            name: '1'
                        },
                        {
                            name: '2'
                        }
                    ],
                    useWorker: false,
                    minRadius: 15,
                    maxRadius: 20,
                    gravity: 1.1,
                    scaling: 1.1,
                    draggable: true,
                    symbolSize: 5,
                    roam: false,
                    large: true, //超过500+的点开启
                    useWorker: true, //超过500+的点开启
                    linkSymbol: 'arrow', //开启箭头指向
                    itemStyle: {
                        normal: {
                            label: {
                                show: false,
                                textStyle: {
                                    color: '#333'
                                }
                            },
                            emphasis: {
                                label: {
                                    show: true
                                }
                            }
                        }
                    },
                    nodes: nodes,
                    links: socailData.links
                }
            ]
        };
        influenceChart.setOption(option);
    }  ,  /**
     * 画力导向(g关系)图
     * */
    drawD3Socail: function (div_id,socailData,mainUserUrn) {
        if(div_id) {
            $("#" + div_id).empty();
        }
        var nodes = socailData.nodes;
        var nodesTemp = eval(JSON.stringify(socailData.nodes));
        var top5 = new Array();
        //根据center值取出排名前五的
        var sortNodes = nodesTemp.sort(function (a, b) {
            return b.in - a.in
        });
        for (var i = 0; i < sortNodes.length && i < 5; i++) {
            top5.push(sortNodes[i]);
        }
        var ld = new leader({
            svgDiv: "#"+div_id,
            width: $("#"+div_id).width(),
            height: $("#"+div_id).height(),
            charge: -200,//越大越集中,越小越分散
            linkDistance: 150,//线长
            imageSize: 50,
            graph: socailData,
            top5:top5,
            mainUserUrn:mainUserUrn,
            radiusMin: 5
        });
        ld.show();
    },
    /**
     *画雷达图
     * */
    drawRadar: function (interestChart, interestData) {
        var intOption = {
            color: ['#57C9CB'],
            tooltip: {
                trigger: 'item',
                formatter: function (params, ticket, callback) {
                    var res = params[0];
                    var value = params[2];
                    var name = params[3];
                    if (params[1] != null && params[1] != '') {
                        res += '<br/>' + params[1] + ' : ' + value[name];
                    } else {
                        for (var i = 0; i < value.length; i++) {
                            res += '<br/>' + name[i].text + ' : ' + value[i];
                        }
                    }
                    var tip = "<div style='width:100%;text-align: left'>" + res + "<div>";
                    return tip;
                }
            },
            polar: [
                {
                    indicator: interestData.indicator
                }
            ],
            series: [
                {
                    name: '我的兴趣',
                    type: 'radar',
                    data: [
                        {value: interestData.series}
                    ]
                }
            ]
        };
        interestChart.setOption(intOption);
    },
    /**
     * 生成圆环图方法
     * param:chart(echart对象),data(数据)
     * */
    drawAgePie: function (chart, data) {
        var option = {
            tooltip: {
                trigger: 'item',
                formatter: "{b} : {c} ({d}%)"
            },
            legend: {
                orient: 'vertical',
                x: 'left',
                y: 'center',
                data: data.legendData
            },
            series: [
                {
                    name: '访问来源',
                    type: 'pie',
                    radius: ['50%', '70%'],
                    center: ['50%', '50%'],
                    itemStyle: {
                        normal: {
                            label: {
                                show: false
                            },
                            labelLine: {
                                show: false
                            }
                        },
                        emphasis: {
                            label: {
                                show: true,
                                position: 'center',
                                textStyle: {
                                    fontSize: '30',
                                    fontWeight: 'bold'
                                }
                            }
                        }
                    },
                    data: data.data
                }
            ]
        };
        chart.setOption(option);
    },
    /**
     * 生成条形图
     * param:chart(echart对象),data(数据)
     * */
    drawBar1: function (chart, data) {
        var option = {
            tooltip: {
                trigger: 'axis',
                //show: true,   //default true
                showDelay: 0,
                hideDelay: 50,
                transitionDuration: 0,
                // borderColor: '#f50',
                borderRadius: 8,
                borderWidth: 2,
                padding: 10,    // [5, 10, 15, 20]
                position: function (p) {
                    return [p[0] + 10, p[1] - 15];
                }
            },
            xAxis: [
                {
                    show: false,
                    type: 'value',
                    boundaryGap: [0, 0.01],
                    splitLine: {show: false}
                }
            ],
            yAxis: [
                {
                    type: 'category',
                    data: data.yAxis,
                    axisLabel: {
                        textStyle: {
                            fontSize: '10'
                        }
                    }
                },
                {
                    type: 'category',
                    data: data.data
                }
            ],
            grid: {
                x: 150,
                y: 20,
                x2:80,
                y2: 30
            },
            series: [
                {
                    type: 'bar',
                    itemStyle: {
                        normal: {
                            color: function (params) {
                                return colors[params.dataIndex];
                            }
                        }
                    },
                    data: data.data
                }
            ]
        }
        chart.setOption(option);
    },
    /**
     * 生成彩虹柱状图
     * param:chart(echart对象),data(数据)
     * */
    drawBar2: function (chart, data) {
        var option = {
            grid: {
                borderWidth: 0,
                x: -1,
                y: 80,
                x2: 2,
                y2: 60
            },
            xAxis: [
                {
                    type: 'category',
                    splitLine: {show: false},
                    data: data.xData,
                    axisTick: {            // 坐标轴小标记
                        show: false      // 每份split细分多少段
                    },
                    axisLabel: {show: false},
                    axisLine: {
                        show: false// 坐标轴线
                    }
                }
            ],
            yAxis: [
                {
                    type: 'value',
                    splitLine: {show: false},
                    axisLabel: {show: false}
                }
            ],
            series: [
                {
                    type: 'bar',
                    barWidth: 25,
                    itemStyle: {
                        normal: {
                            color: function (params) {
                                return colors[params.dataIndex];
                            },
                            label: {
                                show: true,
                                position: 'top',
                                formatter: '{b}\n{c}'
                            }
                        }
                    },
                    data: data.data,
                    //柱子对应的标注点
                    markPoint: {
                        //标注点为自定义图像数据
                        data: data.markData
                    }
                }
            ]
        }
        chart.setOption(option);
    },
    /**
     *绘制高频词云
     * */
    drawWordCloud: function (divId, wordCloudData) {
        /*var wordCloudData = [
            {"size": 66, "text": "大让他队"},
            {"size": 6, "text": "足球"},
            {"size": 3, "text": "曼联"},
            {
                "size": 10,
                "text": "阿森纳"
            }, {"size": 18, "text": "大队"},
            {"size": 23, "text": "aw队"},
            {"size": 11, "text": "dd队"},
            {"size": 12, "text": "fw队"},
            {"size": 13, "text": "访问队"},
            {"size": 11, "text": "大恩恩提队"},

            {"size": 22, "text": "大个人队"},
            {"size": 32, "text": "大环绕队"},
            {"size": 29, "text": "大人烟浩穰队"},
            {"size": 10, "text": "大有人队"},
            {"size": 18, "text": "大发队"},
            {"size": 18, "text": "大那天你队"},
            {"size": 16, "text": "挖掘"},
            {"size": 1, "text": "第二人"}
        ];*/
        $("#"+divId).empty();
        if(!wordCloudData||wordCloudData.length<=0) {
            return;
        }
        var words = wordCloudData.slice(0, 100);
        var cloud = new wordCloud({
            divId: divId,
            words: words,
            twidth: $("#" + divId).width() / 2,
            theight: $("#" + divId).height() / 2,
            cwidth: $("#" + divId).width(),
            cheight: $("#" + divId).height(),
            width: $("#" + divId).width(),
            height: $("#" + divId).height(),
            colorDepth: 0.6,
            colorGray: 1}).show().refresh();
        $("#" + divId).data("plus", cloud);
    }

}

//绘制高频词
function wordCloud(cfg) {
    var divId = cfg.divId;
    $.extend(this, cfg);
    var twidth = cfg.twidth || 150;
    var theight = cfg.theight || 150;
    var cwidth = cfg.cwidth || 400;
    var cheight = cfg.cheight || 400;
    var fill = d3.scale.category20();
    var maxSize = cfg.words[0].size;
    var minSize = cfg.words[cfg.words.length - 1].size;
    var maxFrond = 70;

    var wordScale = d3.scale.linear()
        .domain([minSize, maxSize])
        .range([10, 80]);

    var rgba = function (size) {
        var a = maxFrond * (cfg.colorGray || 2) - size * (cfg.colorGray || 2);
        var r = "rgba(" + a + ", " + a + ", " + a + "," + (cfg.colorDepth || 0.8) + ")";
        return r;
    }

    function refresh() {
        $("#" + divId + " text").hide();
        var arr = $("#" + divId + " text");

        function show(i) {
            $(arr.get(i)).fadeIn("slow");
        }

        function _show(i) {
            return function () {
                show(i);
            }
        }

        for (var i = 0; i < arr.length; i++) {
            setTimeout(_show(i), 200 + i * 40);
        }
        return this;
    }

    this.refresh = refresh;

    function draw(words) {
        d3.select("#" + divId)
            .append("svg")
            .attr("width", cfg.width || 500)
            .attr("height", cfg.height || 500)
            .append("g")
            .attr("transform", "translate(" + twidth + "," + theight + ")")
            .selectAll("text")
            .data(words)
            .enter().append("text")
            .style("font-size", function (d) {
                return d.size + "px";
            })
            .style("font-family", "微软雅黑")
            .style("fill", function (d, i) {
                //return rgba(d.size-20);
                return fill(i);
            }).style("display", "none")
            .attr("text-anchor", "middle")
            .attr("transform", function (d) {
                return "translate(" + [d.x, d.y] + ")rotate(" + d.rotate + ")";
            })
            .text(function (d) {
                return d.text;
            });
    };
    //高频词显示
    this.show = function () {
        d3.layout.cloud().size([cwidth, cheight])
            .words(cfg.words)
            .padding(0)
            .rotate(function () {
                return ~~(Math.random() * 2) * 90;
            })
            .font("微软雅黑")
            .fontSize(function (d, i, j) {
                return parseInt(wordScale(d.size));
                //return d.size*15;
            })//
            .on("end", draw)
            .start();
        return this;

    };
    $("#" + divId).data("words", this);
};

/**
 * 判断n是否存在nArray数组中
 * */
function isEquals(nArray, n,arrKey,nKey) {
    for (var j = 0; j < nArray.length; j++) {
        var temp = nArray[j];
        if (temp[arrKey] == n[nKey]) {
            return true;
        }
    }
    return false;
}

/**
 * 绘制关系图
 * */
function leader(cfg) {
    $.extend(this, cfg);
    this.radiusMin = cfg.radiusMin ? cfg.radiusMin : 1;
    this.width = this.width || 800;
    this.height = this.height || 600;
    this.color = d3.scale.category20c();
    var svg = d3.select(this.svgDiv).append("svg")
        .attr("width", this.width)
        .attr("height", this.height);

    svg.append('svg:defs').append('svg:marker')
        .attr('id', 'markerArrow')
        .attr('viewBox', '0 -5 10 10')
        .attr('refX', 16)
        .attr('markerWidth', 10)
        .attr('markerHeight', 30)
        .attr('orient', 'auto')
        .append('svg:path')
        .attr('d', 'M0,-3L10,0L0,3')
        .attr('fill', '#c2c2c2');

    this.showToolTip = function (userUrn, pMessage, img, pX, pY, pShow) {
        if (typeof(tooltipDivID) == "undefined") {
            tooltipDivID = $('<div id="messageToolTipDiv" class="tipdiv" style=""></div>');
            $('body').append(tooltipDivID);
        }
        if (!pShow) {
            tooltipDivID.hide();
            return;
        }
        //MT.tooltipDivID.empty().append(pMessage);
        tooltipDivID.empty();
        tooltipDivID.append(" <span class='close2' title='关闭'></span>");
        var userId = userUrn.split("-")[1];
        if (img && img.length > 0) {
            tooltipDivID.append("<a style='margin-left:10px;color:#9496A3'  target='_blank' href=\"http://weibo.com/u/" + userId + "\"> <img src=\"" + img + "\" style=\"width:40px;height:40px\"/></a>");
        }
        tooltipDivID.append(pMessage);
        var bdiv = $("<div id='toolTipId' style='text-align:center;margin-top:5px'></div>");

        bdiv.append("<a style='margin-left:10px;color:#9496A3'  target='_blank' href=\"http://weibo.com/u/" + userId + "\">用户主页</a>");
        //bdiv.append("<a style='margin-left:10px' href=\"javascript:delNode('"+userUrn+"')\">删除</a>");
        tooltipDivID.append(bdiv);
        tooltipDivID.css({top: pY, left: pX});
        $(".close2").click(function () {
            tooltipDivID.hide()
        });
        tooltipDivID.show();
    }
    this.show = function () {
        var me = this;

        var force = d3.layout.force()
            .charge(this.charge || -150)
            .linkDistance(this.linkDistance || 100)
            .size([this.width, this.height]);
        if (this.graph) {
            var graph = this.graph;
            force.nodes(graph.nodes)
                .links(graph.links)
                .start();

            //定义连线
            var link = svg.selectAll(".link")
                .data(graph.links)
                .enter()
                .append("line")
                .attr("class", "link")
                .attr("stroke", "#09F")
                .attr("stroke-opacity", "0.8")
                .style("stroke-width", 1);//.attr("marker-end","url(#markerArrow)");

            //定义节点标记
            var node = svg.selectAll(".node")
                .data(graph.nodes)
                .enter()
                .append("circle")
                .attr("class", function (d) {
                    return "node n" + d.group;
                })
                .attr("id", function (d) {
                    return  d.userUrn;
                })
                //设置半径最小值
                .attr("r", function (d) {
                    return  d.r + me.radiusMin;
                })
                .style("fill", function (d) {
                    var col = "";
                    //如果是自己
                    if(me.mainUserUrn&&me.mainUserUrn== d.userUrn){
                        col = "rgb(150, 54, 52)";
                    }else{
                        //排名前五的显示深蓝
                        if (isEquals(me.top5, d,"userUrn","userUrn")) {
                            col = "rgb(22,54,92)";
                        } else {
                            //其他
                            col = "rgb(117, 164, 221)";
                        }
                    }
                    return col;
                }).call(force.drag);

            node.on("mouseover", function (d, i) {
                var title = "昵称:" + d.name + "</br>ID:" + d.userId
                    + "</br>粉丝数:" + d.cntFollowers + "</br>关注数:" + d.cntFollowing + "</br>";
                if (d.in) {
                    title = title + "圈内粉丝数:" + d.in + "</br>";
                }
                /*if (d.followingIds) {
                    title = title + "圈内关注:" + d.followingIds.length + "";
                }*/
                var tt = $(me.svgDiv).offset().top;
                var lt = $(me.svgDiv).offset().left;
                var id = d3.select(this).attr("id");
                me.showToolTip(id, title, d.img, d.x + d3.mouse(this)[0] + 10 + lt, d.y + d3.mouse(this)[1] + 10 + tt, true);
                //console.log(d3.mouse(this));
            })

            node.append("text")
                .attr("dy", ".3em")
                .attr("class", "nodetext")
                .style("text-anchor", "middle")
                .attr("y", 50)
                .text(function (d) {
                    return d.name;
                });

            node.append("title")
                .text(function (d) {
                    return d.name;
                });


            force.on("tick", function () {
                link.attr("x1", function (d) {
                    return d.source.x;
                })
                    .attr("y1", function (d) {
                        return d.source.y;
                    })
                    .attr("x2", function (d) {
                        return d.target.x;
                    })
                    .attr("y2", function (d) {
                        return d.target.y;
                    });

                node.attr("transform", function (d) {
                    return "translate(" + d.x + "," + d.y + ")";
                });
            });
        }
    }
}