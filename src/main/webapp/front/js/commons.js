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
    });
    $('#step_one1 .guanbi').on('click', function() {
        $('#xubox_shade1, #xubox_layer2').hide();
    });
}