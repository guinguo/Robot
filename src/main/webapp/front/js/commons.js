/**
 * Created by guin_guo on 2018/2/28.
 */
$(function () {
    bindEvent();
    console.log('11111')
});

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
    //选择好用户
    goTask = function() {
        console.log('id========' + $('#goTask').val());
        alert($('#goTask').val());
        //1.关闭窗口
        $('#xubox_shade1, #xubox_layer2, #select-user').hide();
        //2.发送add task 请求
        //3.重新加载任务列表
    }
    checkUser = function(user) {
        console.log(user);
        $('#select-user').show();//显示弹窗
        var id = user.split(',')[0];
        var nickname = user.split(',')[1];
        $('#xubox_layer2').hide();//关闭用户列表
        $('#user_nickname').html(nickname);
        $('#goTask').val(id);
    }
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