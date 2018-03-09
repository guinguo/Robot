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
    $('#newTask_add').on('click', function () {
        $('#xubox_shade1, #xubox_layer2').show();
        searchUser(1,5);
    });
    $('#step_one1 .guanbi').on('click', function() {
        $('#xubox_shade1, #xubox_layer2').hide();
    });
    function checkUser(user) {
    }
    function searchUser(num, pageSize, username) {
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
                    var a = '<li class="clearfix border_dashed" onclick="checkUser('+user+')">' +
                                '<div class="fang_user_list"> ' +
                                    '<p class="user_list_weixin clearfix">' +
                                        '<span class="header_02">' +
                                            '<img width="40" height="40" src="'+user.avatar+'">' +
                                        '</span>' +
                                    '</p> ' +
                                    '<i style="height: 16px;">' + user.nickname;
                                    if(user.member>=5) {
                                        a += '<img src='+ctx+'"/front/img/hv.png" style="height: 16px; width: 16px;">';
                                    } else if (user.level>=24) {
                                        a += '<img src='+ctx+'"/front/img/lv.png" style="height: 16px; width: 16px;">';
                                    }
                                    a += '</i><br><div class="clearfix bean_vermicelli"><b class="'+sexClass+'"></b>' +
                                         '<em>粉丝数：'+user.fans+'</em><em class="location_place">'+user.address+'</em>' +
                                    '</div> ' +
                                '</div>' +
                            '</li>';
                    html+=a;
                })
                // $("#work-experience-table>tr:first-child").nextAll().remove();
                $('#user-data').html(html);
            },
            error:function(data) {
                alert("error");
            }
        });
    }
}