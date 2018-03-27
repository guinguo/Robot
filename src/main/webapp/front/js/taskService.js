//info信息
detail.service("userDimensionInfoModelService", function () {
    var createUserDimensionInfo = function (userDimensionChart, userDimensionInfo) {
        var func = (function () {
            return {
                //匹配全部数据
                show: function () {
                    try {
                        preferWordsInfo.func.show();
                        myInterestsInfo.func.show();
                        myFansInfo.func.show();
                        socialCircleInfo.func.show();
                    } catch (e) {

                    }
                },
                //显示对应的图表
                showInfoChartByModelId: function (modelId) {
                    var $model = $("#" + modelId + "");
                    //如果加载完成
                    if (eval(modelId + "Info.taskHasDone")) {
                        $model.show();
                        userDimensionChart[modelId].func.show();
                    }
                },
                //显示全部图表
                showAllInfoChart: function () {

                    //没打开过
                    func.showInfoChartByModelId("preferWords");
                    func.showInfoChartByModelId("myInterests");
                    func.showInfoChartByModelId("socialCircle");
                    func.showInfoChartByModelId("myFans");
                    //默认移动到最爱说
                    userDimensionChart.func.moveOneDivToDimensionChartDivs(["preferWords","myInterests", "socialCircle",  "myFans"]);

                }

            }
        })();

        var event = (function () {
            return {
                clickInfoModel: function (modelId) {
                    var $model = $("#" + modelId);
                    //如果第一次打开就加载数据
                    if ($model.css("display") == 'none') {
                        func.showInfoChartByModelId(modelId);
                        userDimensionChart.func.moveToDimensionChartDiv(modelId);
                    } else
                    //不加载数据
                    {
                        userDimensionChart.func.moveToDimensionChartDiv(modelId);
                    }
                }
            }
        })();


        //我最爱说
        var preferWordsInfo = {
            func: (function () {
                return {
                    //匹配我最爱说数据
                    show: function () {
                        try {
                            preferWordsInfo.list = userDimensionInfo.preferWordsInfo.item;
                            preferWordsInfo.taskHasDone = userDimensionInfo.preferWordsInfo.taskHasDone;

                        } catch (e) {

                        }
                    }
                }
            })(),
            event: (function () {
                return {}
            })(),
            taskHasDone: false,
            list: [],//["1", "2", "3"]
        };
        //我的兴趣
        var myInterestsInfo = {
            func: (function () {
                return {
                    //匹配我的兴趣数据
                    show: function () {
                        try {
                            myInterestsInfo.cutText = userDimensionInfo.myInterestsInfo.cutText;
                            myInterestsInfo.text = userDimensionInfo.myInterestsInfo.text;

                            myInterestsInfo.taskHasDone = userDimensionInfo.myInterestsInfo.taskHasDone;
                        } catch (e) {

                        }
                    }
                }
            })(),
            event: (function () {

            })(),
            taskHasDone: false,
            text: "",//1 1 1
            cutText: ""//1 2 3...
        };
        //我的粉丝
        var myFansInfo = {
            func: (function () {
                return {
                    //匹配我的粉丝数据
                    show: function () {
                        try {
                            myFansInfo.concentrateUpon = userDimensionInfo.myFansInfo.concentrateUpon;
                            myFansInfo.more = userDimensionInfo.myFansInfo.more;
                            myFansInfo.whatAreTheyLike = userDimensionInfo.myFansInfo.whatAreTheyLike;
                            myFansInfo.taskHasDone = userDimensionInfo.myFansInfo.taskHasDone;
                        } catch (e) {

                        }
                    }
                }
            })(),
            event: (function () {

            })(),
            taskHasDone: false,
            //集中在
            concentrateUpon: "",//江苏
            //更多 男的更多还是女的更多
            more: "",//未知
            //TA们喜欢什么
            whatAreTheyLike: {
                text: "",//1 1 1
                cutText: ""//1 2 3...
            }
        };
        //社交圈
        var socialCircleInfo = {
            func: (function () {
                return {
                    //匹配社交圈数据
                    show: function () {
                        try {
                            socialCircleInfo.text = userDimensionInfo.socialCircleInfo.text;
                            socialCircleInfo.cutText = userDimensionInfo.socialCircleInfo.cutText;
                            socialCircleInfo.taskHasDone = userDimensionInfo.socialCircleInfo.taskHasDone;
                        } catch (e) {

                        }
                    }
                }
            })(),
            event: (function () {

            })(),
            taskHasDone: false,
            text: "",//1 1 1
            cutText: ""//1 2 3...
        };
        return {
            //自身的方法
            func: func,
            //事件:
            event: event,
            //我最爱说
            preferWordsInfo: preferWordsInfo,
            //我的兴趣
            myInterestsInfo: myInterestsInfo,
            //我的粉丝
            myFansInfo: myFansInfo,
            //社交圈
            socialCircleInfo: socialCircleInfo
        }
    };
    return {
        createUserDimensionInfo: createUserDimensionInfo
    };
});