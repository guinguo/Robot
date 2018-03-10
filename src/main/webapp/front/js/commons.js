/**
 * Created by guin_guo on 2018/2/28.
 */
$(function () {
    loadTasks();
    initFun();
    bindEvent();
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
function loadTasks(num, pageSize, status) {
    deleteTask = function(id) {

        var confirmButton = {};
        confirmButton.confirm = {
            label: "确认",
            className: "btn-primary primary",
            callback: function () {
                newTitle = '删除任务';
                $.ajax({
                    type: "POST",
                    url: ctx + "/deleteTask",
                    data:{
                        id:id
                    },
                    success: function (msg) {
                        filterTask();
                    },
                    error: function (msg) {
                        bootbox.alert('删除任务失败');
                    }
                });
            }
        };
        confirmButton.cancel = {
            label: "取消",
            className: "btn-default"
        };
        bootbox.dialog({
            message: "<label class='delete-font delete-font-msg text-danger text-center'>您确定要删除任务？</label>",
            title: "<strong class='delete-font'>删除任务</strong>",
            local: 'zh_CN',
            onEscape: true,
            closeButton: true,
            buttons: confirmButton,
            backdrop: true
        });

        /*$.ajax({
            type: "POST",
            url: ctx + "/deleteTask",
            data:{
                id:id
            },
            success: function(data) {
                filterTask();
            },
            error:function(data) {
                alert("删除任务失败！");
            }
        });*/
    };
    $.ajax({
        type: "GET",
        url: ctx + "/tasks",
        dataType: "json",
        data:{
            num:num,
            pageSize:pageSize,
            status:status,
        },
        success: function(data) {
            var tasks = data.datas;
            var html = '';
            tasks.forEach(function(task, idx, array){
                var taksId = task.id;
                var user = task.user;
                var li = '<li id="taskLi" ' + taksId + ' class="thumbnail-box clearfix">' +
                            '<div class="box3">' +
                                '<a target="_blank" href="'+ctx+'/task/detail?id='+taksId+'">' +
                                    '<div class="thumbnail-left">' +
                                        '<div class="circle-text">' +
                                            '<img src="'+user.avatar+'">' +
                                        '</div>' +
                                        '<p>已完成 <b class="pink">'+task.status+'%</b></p>' +
                                    '</div>' +
                                    '<div class="thumbnail-right">' +
                                        '<div class="user-name clearfix">' +
                                            '<b title="'+user.nickname+'">'+user.nickname+'' + (user.level>=24 ? '<img src="'+ctx+'/front/img/dr.png">' : '') + '</b>' +
                                        '</div>' +
                                        '<dl class="user-profile">' +
                                            '<dt><span>关注：</span>'+user.focus+'</dt>' +
                                            '<dt><span>粉丝：</span>'+user.fans+'</dt>' +
                                            '<dt><span>地区：</span>'+user.address+'</dt>' +
                                            '<dt>'+new Date(task.createDate).Format("yyyy-MM-dd HH:mm")+'</dt>' +
                                        '</dl>' +
                                    '</div>' +
                                '</a>' +
                                '<a tid="12868" class="guanbi" onclick="deleteTask(&quot;'+task.id+'&quot;)"></a>' +
                            '</div>' +
                        '</li>'
                html+=li;
            });
            $('#allTaskList').html(html);
            $('.box3').hover(function () {
                $(this).find('a.guanbi').show();
            },function(){
                $(this).find('a.guanbi').hide();
            });
        },
        error:function(data) {
            alert("获取用户数据失败！");
        }
    });
}
filterTask = function (staus) {
    $('.task-status li a').removeClass('current');
    if(staus) {
        if(staus == 20) {
            $('.task-status li a.l3').addClass('current');
        } else{
            $('.task-status li a.l2').addClass('current');
        }
    } else {
        $('.task-status li a.l1').addClass('current');
    }
    loadTasks(1, 20, staus);
};
function initFun() {
    //选择好用户
    goTask = function() {
        var uid = $('#goTask').val();
        console.log('go task id =' + uid);
        //1.关闭窗口
        $('#xubox_shade1, #xubox_layer2, #select-user').hide();
        //2.发送add task 请求
        if (uid) {
            $.ajax({
                type: "POST",
                url: ctx + "/addTasks",
                data:{
                    uid:uid
                },
                success: function(data) {
                    console.log(data);
                    //3.重新加载任务列表
                    filterTask()
                },
                error:function(data) {
                    alert("创建任务失败！");
                }
            });
        }
    };
    checkUser = function(user) {
        console.log(user);
        $('#select-user').show();//显示弹窗
        var id = user.split(',')[0];
        var nickname = user.split(',')[1];
        $('#xubox_layer2').hide();//关闭用户列表
        $('#user_nickname').html(nickname);
        $('#goTask').val(id);
    };
}
function bindEvent() {
    $('p.common-nav-title').hover(function(){
        $('ul.common-navs').show();
    },function(){
        $('ul.common-navs').hide();
    });
    //点击+任务
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
    }
}