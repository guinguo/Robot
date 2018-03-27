detail.service("userDimensionChartModelService", function () {
    var createUserDimensionChart = function (userDimensionChart) {
        /**
         * 当前模块的方法和事件
         */
        var func = (function () {
            return {
                //通过对应的taskId和moduleId下载图表excle数据
                downloadExcleByTaskIdAndModuleId: function (taskId, moduleId) {
                    window.open(context + "/kol/export/" + taskId + "/" + moduleId + ".do");
                },
                //通过id关闭某个图表.
                removeChartByChartId: function (id) {
                    $("#" + id).hide(500);
                },
                /**
                 * 这个DIV必须是显示状态
                 * 滚动条移动到当前DIV
                 * @param domId
                 */
                moveToDimensionChartDiv: function (domId) {
                    var offset = $('#' + domId).offset();
                    if (!offset||!offset.top) {
                        return;
                    }
                    $("html,body").animate({scrollTop: offset.top - 5}, 500);
                },
                /**
                 * 滚动条按顺序移动到当前DIV列表中的某一个
                 * @param domIds
                 */
                moveOneDivToDimensionChartDivs: function (domIds) {
                    for (var i = 0; i < domIds.length; i++) {
                        var domId = domIds[i];
                        var offset = $('#' + domId).offset();
                        if (offset&&offset.top) {
                            $("html,body").animate({scrollTop: offset.top - 5}, 500);
                            break;
                        }
                    }

                },
                createDownloadWordCloudHref: function (divBut, datas, name) {
                    var scale = d3.scale.linear();
                    scale.domain([10, 90]).range([10, 20]);
                    var fill = d3.scale.category20b();
                    var w = 900;
                    var h = 600;
                    var canvas = document.createElement("canvas"),
                        c = canvas.getContext("2d");
                    canvas.width = w;
                    canvas.height = h;
                    c.translate(w >> 1, h >> 1);
                    //c.scale(scale, scale);
                    jQuery.each(datas, function (i, word) {
                        if (word.x && word.y) {
                            c.save();
                            c.translate(word.x, word.y);
                            c.rotate(word.rotate * Math.PI / 180);
                            c.textAlign = "center";
                            c.fillStyle = fill(word.text.toLowerCase());
                            c.font = word.size + "px " + word.font;
                            c.fillText(word.text, 0, 0);
                            c.restore();
                        }
                    })
                    c.translate(0.5, 0.5);
                    c.lineWidth = 1;
                    c.beginPath();
                    c.moveTo(-210, 180);
                    c.lineTo(230, 180);
                    c.strokeStyle = "#666";
                    c.stroke();
                    //设置字体样式
                    c.font = "14px 宋体";
                    //设置字体填充颜色
                    c.fillStyle = "#666";
                    //从坐标点(50,50)开始绘制文字
                    c.fillText("@BlueMC Marketing Platform                      www.bluemc.cn", -200, 200);
                    $("#" + divBut).attr("href", canvas.toDataURL("image/png"));
                    $("#" + divBut).attr('download', name + new Date().format("yyyyMMdd") + '.png');
                }
            }
        })();

        var event = (function () {
            return {
                clickRemoveBtn: function (id) {
                    func.removeChartByChartId(id);
                },
                }
        })();

        /**
         * 子模块
         */
        //我最爱说
        var preferWords = {
            func: (function () {
                return {
                    //加载我最爱说数据
                    show: function () {
                        try {
                            preferWords.top5.func.show();
                            preferWords.wordCloud.func.show();

                        } catch (e) {
                            console.error(e)

                        }
                    }
                }
            })(),
            event: (function () {

            })(),

            top5: {
                func: (function () {
                    return {
                        //加载我最爱说数据
                        show: function () {
                            try {
                                //top5
                                preferWords.top5.latest.list = userDimensionChart.preferWords.top5.latest;
                                preferWords.top5.mostComments.list = userDimensionChart.preferWords.top5.mostComments;
                                preferWords.top5.mostForwards.list = userDimensionChart.preferWords.top5.mostForwards;
                                preferWords.top5.mostLikes.list = userDimensionChart.preferWords.top5.mostLikes;
                            } catch (e) {
                                console.error(e)

                            }
                        }
                    }
                })(),
                event: (function () {
                    return {
                        //加载我最爱说数据
                        changeTab: function (key) {
                            try {
                                preferWords.top5.active = key;
                            } catch (e) {
                                console.error(e)

                            }
                        }
                    }
                })(),
                //下载key
                downloadKey: 'Wzas',
                //激活状态
                active: "latest",
                //最新
                latest: {
                    list: [
                        {
                            //日期
                            date: "2015-6-10",
                            //帖子文本
                            postText: "612把#盗墓笔记S#交给你们 613把吴邪交给快本 #612盗笔盛大开墓#",
                            postCutText: "最新...",
                            postUrl: "http://www.baidu.com"
                        }, {
                            //日期
                            date: "2015-6-10",
                            //帖子文本
                            postText: "612把#盗墓笔记S#交给你们 613把吴邪交给快本 #612盗笔盛大开墓#",
                            postCutText: "最新...",
                            postUrl: "http://www.baidu.com"
                        }
                    ]
                },
                //评论最多
                mostComments: {
                    list: [
                        {
                            //评论次数
                            commentsSize: 321,
                            //帖子文本
                            postText: "612把#盗墓笔记S#交给你们 613把吴邪交给快本 #612盗笔盛大开墓#",
                            postCutText: "评论最多...",
                            postUrl: "http://www.baidu.com"
                        }, {
                            //评论次数
                            commentsSize: 66,
                            //帖子文本
                            postText: "612把#盗墓笔记S#交给你们 613把吴邪交给快本 #612盗笔盛大开墓#",
                            postCutText: "评论最多...",
                            postUrl: "http://www.baidu.com"
                        }
                    ]
                },
                //转发最多
                mostForwards: {
                    list: [
                        {
                            //转发次数
                            forwardSize: 321,
                            //帖子文本
                            postText: "612把#盗墓笔记S#交给你们 613把吴邪交给快本 #612盗笔盛大开墓#",
                            postCutText: "转发最多...",
                            postUrl: "http://www.baidu.com"
                        }, {
                            //转发次数
                            forwardSize: 55,
                            //帖子文本
                            postText: "612把#盗墓笔记S#交给你们 613把吴邪交给快本 #612盗笔盛大开墓#",
                            postCutText: "转发最多...",
                            postUrl: "http://www.baidu.com"
                        }
                    ]
                },
                //点赞最多
                mostLikes: {
                    list: [
                        {
                            //点赞次数
                            likeSize: 33,
                            //帖子文本
                            postText: "612把#盗墓笔记S#交给你们 613把吴邪交给快本 #612盗笔盛大开墓#",
                            postCutText: "点赞最多...",
                            postUrl: "http://www.baidu.com"
                        }, {
                            //点赞次数
                            likeSize: 321,
                            //帖子文本
                            postText: "612把#盗墓笔记S#交给你们 613把吴邪交给快本 #612盗笔盛大开墓#",
                            postCutText: "点赞最多...",
                            postUrl: "http://www.baidu.com"
                        }
                    ]
                }
            },//top5完结

            //高频词云
            wordCloud: {
                func: (function () {
                    return {
                        //加载我最爱说数据
                        show: function () {
                            try {
                                //词云数据
                                preferWords.wordCloud.weiBoWord.for90.words = userDimensionChart.preferWords.wordCloud.weiBoWordFor90;
                                preferWords.wordCloud.weiBoWord.for30.words = userDimensionChart.preferWords.wordCloud.weiBoWordFor30;
                                preferWords.wordCloud.mention.for90.words = userDimensionChart.preferWords.wordCloud.mentionFor90;
                                preferWords.wordCloud.mention.for30.words = userDimensionChart.preferWords.wordCloud.mentionFor30;
                                //填充
                                preferWords.wordCloud.func.createWordCloudChart();
                                preferWords.wordCloud.func.createDownloadwordCloudChart();
                            } catch (e) {
                                console.error(e);

                            }
                        },
                        //创建词云图
                        createWordCloudChart: function () {
                            //显示词云
                            kolEchart.drawWordCloud("wordCloud", preferWords.wordCloud[preferWords.wordCloud.active][preferWords.wordCloud.dayActive].words);
                        },
                        //创下载Key
                        createDownloadKey: function () {
                            preferWords.wordCloud.downloadKey = preferWords.wordCloud.active + preferWords.wordCloud.dayActive;
                            //显示词云
                            kolEchart.drawWordCloud("wordCloud", preferWords.wordCloud[preferWords.wordCloud.active][preferWords.wordCloud.dayActive].words);
                        },
                        //解析词云数据并绘制出canvas的64位图片下载链接,并赋值在标签
                        createDownloadwordCloudChart: function () {
                            func.createDownloadWordCloudHref("a_gpcy", preferWords.wordCloud[preferWords.wordCloud.active][preferWords.wordCloud.dayActive].words, preferWords.wordCloud.nameActive);
                        }

                    }

                })(),
                event: (function () {
                    return {
                        //点击Tab
                        clickCloudTab: function (tabKey) {
                            //设置tab状态
                            preferWords.wordCloud.active = tabKey;
                            preferWords.wordCloud.nameActive = "weiBoWord" == tabKey ? "高频词云" : "品牌提及";
                            //创建词云图
                            preferWords.wordCloud.func.createWordCloudChart();
                            preferWords.wordCloud.func.createDownloadKey();
                            preferWords.wordCloud.func.createDownloadwordCloudChart();
                        },
                        //点击时间Tab
                        clickCloudDayTab: function (tabKey) {
                            //设置tab状态
                            preferWords.wordCloud.dayActive = tabKey;
                            //创建词云图
                            preferWords.wordCloud.func.createWordCloudChart();
                            preferWords.wordCloud.func.createDownloadKey();
                            preferWords.wordCloud.func.createDownloadwordCloudChart();
                        }
                    }

                })(),
                //下载key
                downloadKey: 'weiBoWordfor30',
                //激活状态
                active: "weiBoWord",
                //时间激活状态
                dayActive: "for30",
                //显示词云名称
                nameActive: "高频词云",
                weiBoWord: {
                    //近90天微博
                    for90: {
                        words: []
                    },
                    //近30天微博
                    for30: {
                        words: []
                    }
                },
                mention: {
                    //近90天提及
                    for90: {
                        words: []
                    },
                    //近30天提及
                    for30: {
                        words: []
                    }
                },

            }//高频词云完结

        };

        //我的兴趣
        var myInterests = {
            func: (function () {
                return {
                    show: function () {
                        //加载数据
                        myInterests.list = userDimensionChart.myInterests.myInterestsItemses;
                        //显示图表
                        try {
                            kolEchart.drawRadar(kolEchart.getChart("interest"), formatData.formatInterest(myInterests.list));
                        } catch (e) {
                            console.error(e)
                        }

                    }
                }
            })(),
            event: (function () {

            })(),
            //下载key
            downloadKey: 'Wdxq',
            active: false,
            list: [
                {
                    text: " ",
                    score: 0
                }
            ]
        };

        var myFans = {
            func: (function () {
                return {
                    show: function () {
                        myFans.fansNum = userDimensionChart.myFans.fansNum;
                        myFans.sexRatio.func.show();
                        myFans.arealDistribution.func.show();
                        myFans.age.func.show();
                        myFans.constellation.func.show();
                        myFans.schoolRank.func.show();
                        myFans.jobTag.func.show();
                        myFans.interestDistribution.func.show();
                        myFans.commonConcern.func.show();
                        myFans.userType.func.show();
                        myFans.verifiReason.func.show();

                        myFans.hierarchicalDistribution.func.show();
                        myFans.fansRange.func.show();

                    }
                }
            })(),
            event: (function () {

            })(),
            active: false,
            fansNum: 0,
            //男女比例
            sexRatio: {
                func: (function () {
                    return {
                        show: function () {
                            myFans.sexRatio.man = userDimensionChart.myFans.sexRatio.man;
                            myFans.sexRatio.woman = userDimensionChart.myFans.sexRatio.woman;
                            myFans.sexRatio.unknown = userDimensionChart.myFans.sexRatio.unknown;
                        }
                    }
                })(),
                //下载key
                downloadKey: 'Nnbl',
                //男
                man: {
                    number: 123,
                    ratio: "12.2%"
                },
                //女
                woman: {
                    number: 88,
                    ration: "87.7%"
                },
                //未知
                unknown: {
                    number: 88,
                    ration: "87.7%"
                },
            },
            //地域分布
            arealDistribution: {
                func: (function () {
                    return {
                        mappingData: function () {
                            //省份
                            myFans.arealDistribution.province.srealDistributionItems = userDimensionChart.myFans.arealDistribution.province.arealDistributionItems;
                            myFans.arealDistribution.province.topXArealDistributionItems = userDimensionChart.myFans.arealDistribution.province.topXArealDistributionItems;
                            //城市
                            myFans.arealDistribution.city.srealDistributionItems = userDimensionChart.myFans.arealDistribution.city.arealDistributionItems;
                            myFans.arealDistribution.city.topXArealDistributionItems = userDimensionChart.myFans.arealDistribution.city.topXArealDistributionItems;
                        },
                        show: function () {
                            myFans.arealDistribution.func.mappingData();
                            try {
                                kolEchart.drawMap(kolEchart.getChart("fansArealDistribution"), myFans.arealDistribution[myFans.arealDistribution.active].srealDistributionItems);
                            } catch (e) {
                                console.error(e)
                            }
                        }
                    }
                })(),
                event: (function () {
                    return {
                        clickarealTab: function (tabKey) {
                            myFans.arealDistribution.active = tabKey;
                            myFans.arealDistribution.downloadKey = tabKey;
                            myFans.arealDistribution.func.show();
                        }
                    }
                })(),
                //下载key
                downloadKey: 'province',
                //激活状态
                active: "province",
                //展示图表要用的数据
                //省份
                province: {
                    srealDistributionItems: [
                        {
                            name: "",//未知
                            value: 0
                        }
                    ],
                    //展示排序后的前几
                    topXArealDistributionItems: [
                        {
                            name: "",//未知
                            value: 0
                        }
                    ]
                },
                //城市
                city: {
                    srealDistributionItems: [
                        {
                            name: "",//未知
                            value: 0
                        }
                    ],
                    //展示排序后的前几
                    topXArealDistributionItems: [
                        {
                            name: "",//未知
                            value: 0
                        }
                    ]
                }
            },
            //年龄分布
            age: {
                func: (function () {
                    return {
                        mappingData: function () {
                            myFans.age.jsonNode = userDimensionChart.myFans.age;
                            //console.error(myFans.age.jsonNode);
                        },
                        show: function () {
                            myFans.age.func.mappingData();
                            try {
                                kolEchart.drawAgePie(kolEchart.getChart("age"), formatData.formatAgeData(myFans.age.jsonNode));
                            } catch (e) {
                                console.error(e)
                            }
                        }
                    }
                })(),
                event: (function () {
                    return {}
                })(),
                //下载key
                downloadKey: 'Nlfb',
                jsonNode: {}
            },
            //星座
            constellation: {
                func: (function () {
                    return {
                        mappingData: function () {
                            myFans.constellation.jsonNode = userDimensionChart.myFans.constellation;
                        },
                        show: function () {
                            myFans.constellation.func.mappingData();
                            try {
                                kolEchart.drawBar2(kolEchart.getChart("constellation"), formatData.formatConstellation(myFans.constellation.jsonNode));
                            } catch (e) {
                                console.error(e)
                            }
                        }
                    }
                })(),
                event: (function () {
                    return {}
                })(),
                //下载key
                downloadKey: 'Xzfb',
                jsonNode: {}
            },
            //学校排名
            schoolRank: {
                func: (function () {
                    return {
                        mappingData: function () {
                            myFans.schoolRank.jsonNode = userDimensionChart.myFans.schoolRank;
                        },
                        show: function () {
                            myFans.schoolRank.func.mappingData();
                            try {
                                kolEchart.drawBar1(kolEchart.getChart("school_rank"), formatData.formatSchoolData(myFans.schoolRank.jsonNode));
                            } catch (e) {
                                console.error(e)
                            }
                        }
                    }
                })(),
                event: (function () {
                    return {}
                })(),
                //下载key
                downloadKey: 'Xxpm',
                jsonNode: {}
            },
            //职业标签
            jobTag: {
                func: (function () {
                    return {
                        show: function () {
                            try {
                                //词云数据(暂时注释)
                                myFans.jobTag.words = userDimensionChart.myFans.jobTag;
                                //填充
                                myFans.jobTag.func.createJobTagWordCloudChart();

                                //解析词云数据并绘制出canvas的64位图片下载链接,并赋值在标签
                                func.createDownloadWordCloudHref("a_zybq", myFans.jobTag.words, '职业标签_');
                            } catch (e) {
                                console.error(e);
                            }
                        },
                        //创建词云图
                        createJobTagWordCloudChart: function () {
                            kolEchart.drawWordCloud("jobTag", myFans.jobTag.words);
                        }
                    }

                })(),
                event: (function () {

                })(),
                //下载key
                downloadKey: 'Zybq',
                words: []
            },
            //兴趣分布top10
            interestDistribution: {
                func: (function () {
                    return {
                        show: function () {
                            myFans.interestDistribution.list = userDimensionChart.myFans.interestDistribution.topXInterestDistributionItems;
                        }
                    }
                })(),
                //下载key
                downloadKey: 'Xqfb',
                list: [
                    {
                        //兴趣领域
                        domain: "日韩娱乐",
                        //分值
                        score: 0.0,
                        //兴趣比例
                        ration: "15.5%"
                    }
                ]
            },
            //共同关注top10
            commonConcern: {
                func: (function () {
                    return {
                        show: function () {
                            myFans.commonConcern.commonConcernItems = userDimensionChart.myFans.commonConcern.commonConcernItems;
                            myFans.commonConcern.topXCommonConcernItems = userDimensionChart.myFans.commonConcern.topXCommonConcernItems;
                        }
                    }
                })(),
                //下载key
                downloadKey: 'Gtgz',
                commonConcernItems: [
                    {
                        //共同关注人名字
                        concernUserName: "嘻嘻嘻",
                        //共同关注人截取过的名字
                        cutConcernUserName: "嘻嘻嘻",
                        //共同关注人url
                        concernUserWeiboHomeUrl: "http://www.baidu.com",
                        //
                        concernUserWeiboImageUrl: "http://tp3.sinaimg.cn/1760294773/180/40004332098/1.baidu.com",
                        //关注用户数
                        concernUserNumber: "100%",
                        //关注比例
                        ration: "3332"
                    }
                ],
                topXCommonConcernItems: [
                    {
                        //共同关注人名字
                        concernUserName: "嘻嘻嘻",
                        //共同关注人url
                        concernUserWeiboHomeUrl: "http://www.baidu.com",
                        //
                        concernUserWeiboImageUrl: "http://tp3.sinaimg.cn/1760294773/180/40004332098/1.baidu.com",
                        //关注用户数
                        concernUserNumber: "100%",
                        //关注比例
                        ration: "3332"
                    }
                ]
            },
            //微博身份
            userType: {
                func: (function () {
                    return {
                        mappingData: function () {
                            myFans.userType.jsonNode = userDimensionChart.myFans.userType;
                        },
                        show: function () {
                            myFans.userType.func.mappingData();
                            try {
                                kolEchart.drawAgePie(kolEchart.getChart("user_type"), formatData.formatAgeData(myFans.userType.jsonNode));
                            } catch (e) {
                                console.error(e)
                            }
                        }
                    }
                })(),
                event: (function () {
                    return {}
                })(),
                //下载key
                downloadKey: 'Wbsf',
                jsonNode: {}
            },
            //认证原因
            verifiReason: {
                func: (function () {
                    return {
                        show: function () {
                            try {
                                //词云数据
                                myFans.verifiReason.words = userDimensionChart.myFans.verifiReason;
                                //填充
                                myFans.verifiReason.func.createVerifiReasonWordCloudChart();
                                //解析词云数据并绘制出canvas的64位图片下载链接,并赋值在标签
                                func.createDownloadWordCloudHref("a_rzyy", myFans.verifiReason.words, '认证原因_');
                            } catch (e) {
                                console.error(e)
                            }
                        },
                        //创建词云图
                        createVerifiReasonWordCloudChart: function () {
                            kolEchart.drawWordCloud("verifiReason", myFans.verifiReason.words);
                        }
                    }

                })(),
                event: (function () {

                })(),
                //下载key
                downloadKey: 'Rzyy',
                words: []
            },
            //等级分布
            hierarchicalDistribution: {
                func: (function () {
                    return {
                        show: function () {
                            //映射数据
                            myFans.hierarchicalDistribution.list = userDimensionChart.myFans.hierarchicalDistribution.hierarchicalDistributionItems;
                            try { //格式化图表
                                var data = formatData.formatLevelData(myFans.hierarchicalDistribution.list);
                                //图表echart对象
                                var levelChart = kolEchart.getChart("level");
                                //画图
                                kolEchart.drawBar(levelChart, data.lvchartData, data.xData);
                            } catch (e) {
                                console.error(e)
                            }
                        }
                    }
                })(),
                //下载key
                downloadKey: 'Djfb',
                list: [
                    {
                        count: 0,//555
                        level: ""//11-22
                    }
                ]
            },
            //粉丝区间
            fansRange: {
                func: (function () {
                    return {
                        mappingData: function () {
                            //myFans.fansRange.jsonNode = userDimensionChart.myFans.fansRange;
                            var keys=[];
                            for(var key in userDimensionChart.myFans.fansRange){
                                keys.push(key);
                            }
                            keys.reverse();
                            var sortble={},key;
                            for(var i in keys){
                                key=keys[i];
                                sortble[key]=userDimensionChart.myFans.fansRange[key];
                            }
                            myFans.fansRange.jsonNode = sortble;
                            //console.info(myFans.fansRange.jsonNode);
                        },
                        show: function () {
                            myFans.fansRange.func.mappingData();
                            try {
                                kolEchart.drawBar1(kolEchart.getChart("fans_range"), formatData.formatFansRangeData(myFans.fansRange.jsonNode));
                            } catch (e) {
                                console.error(e)
                            }
                        }
                    }
                })(),
                event: (function () {
                    return {}
                })(),
                //下载key
                downloadKey: 'Fsqj',
                jsonNode: {}
            }
        };

        var socialCircle = (function () {
            return {
                func: (function () {
                    return {
                        mappingData: function () {
                            socialCircle.influenceTop5.list = userDimensionChart.socialCircle.influenceTop5.influenceTop5Items;
                            socialCircle.relationship = userDimensionChart.socialCircle.relationship;
                            socialCircle.allUserNumber = userDimensionChart.socialCircle.allUserNumber;
                        },
                        show: function () {
                            socialCircle.func.mappingData();

                            //Echart实现
                            //kolEchart.drawEchartSocail(kolEchart.getChart("influnce"), socialCircle.relationship);
                            //d3实现
                            kolEchart.drawD3Socail("influnce",  socialCircle.relationship,mainUserUrn||null);
                        }
                    }
                })(),
                //下载key
                downloadKey: 'Qngz',
                active: false,
                //总用户数
                allUserNumber: 0,
                //影响力TOP5
                influenceTop5: {
                    list: [
                        {
                            //名字
                            userScreenName: "",//张杰
                            //截取后的名字
                            cutScreenName:"",
                            //微博链接
                            userWeiboHomeUrl: "",//http://www.baidu.com
                            //微博头像链接
                            userWeiboHomeImageUrl: "",//http://tp3.sinaimg.cn/1220924217/50/40004332098/1
                            //影响力分数
                            influenceNumber: 15
                        }
                    ]
                },
                //关注关系图
                relationship: {}
            }
        })();


        return {
            //自身的方法
            func: func,
            //事件:
            event: event,
            //我最爱说
            preferWords: preferWords,//我最爱说

            //我的兴趣
            myInterests: myInterests,//我的兴趣完结

            //我的粉丝
            myFans: myFans,//我的粉丝完结

            //社交圈
            socialCircle: socialCircle//社交圈完结
        }

    };
    return {
        createUserDimensionChart: createUserDimensionChart
    }
})