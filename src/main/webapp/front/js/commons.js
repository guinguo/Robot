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
}