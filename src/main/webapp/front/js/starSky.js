/**
 * Created by guin_guo on 2016/2/26.
 */
window.onload = function (){
    NProgress.start();
    NProgress.done() ;
    /**
     * 增加鼠标事件，鼠标移上时显示文字，移走时隐藏文字
     */
    var datas = document.getElementsByClassName("datas_img");
    var datas2 = document.getElementsByClassName("img_word");
    for(var i =0; i<datas.length;i++) {
        datas[i].onmouseover = handelEvent;
        datas[i].onmouseout = handelEvent;
    }
    for(var i =0; i<datas2.length;i++) {
        datas2[i].onmouseover = handelEvent;
        datas2[i].onmouseout = handelEvent;
    }
}
function handelEvent(e){
    var parent = this.parentNode;
    var img = parent.getElementsByTagName("img");
    var ie = (window.ActiveXObject) ? true : false;
    var words = parent.getElementsByTagName("span");
    if(e.type=="mouseover"){
        if(checkHover(e,this)){
            var n = 10;
            if(ie){
                img[0].style.cssText = "filter:alpha(opacity="+n+")";
            }else{
                img[0].style.opacity = "0."+n;
            }
            words[0].style.position="absolute";
            words[0].style.height = this.height*0.5+"px";
            words[0].style.width = this.width*0.8+"px";
            words[0].style.left = pageX(this.parentNode)+15+"px";
            words[0].style.top = pageY(this.parentNode)-550+"px";
            words[0].style.display = "block";
        }
    } else if(e.type=="mouseout"){
        if(checkHover(e,this)){
            if(ie){
                img[0].style.cssText = "filter:alpha(opacity="+0+")";
            }else{
                img[0].style.opacity = 100;
            }
            words[0].style.display = "none";
        }
    }
    e.stopPropagation();
}
function contains(parentNode, childNode) {
    if (parentNode.contains) {
        return (parentNode != childNode) && (parentNode.contains(childNode));
    } else {
        return !!(parentNode.compareDocumentPosition(childNode) & 16);
    }
}
function checkHover(e,target){
    if (getEvent(e).type=="mouseover")  {
        return !contains(target,getEvent(e).relatedTarget||getEvent(e).fromElement) && !((getEvent(e).relatedTarget||getEvent(e).fromElement)===target);
    } else {
        return !contains(target,getEvent(e).relatedTarget||getEvent(e).toElement) && !((getEvent(e).relatedTarget||getEvent(e).toElement)===target);
    }
}
function getEvent(e){
    return e||window.event;
}

// 获取元素的水平位置
function pageX(elem) {
    var p = 0;
    // 累加每一个父元素的偏移量
    while ( elem.offsetParent ) {
        // 增加偏移量
        p += elem.offsetLeft;

        // 遍历下一个父元素
        elem = elem.offsetParent;
    }
    return p;
}

// 获取元素的垂直位置
function pageY(elem) {
    var p = 0;
    // 累加每一个父元素的偏移量
    while ( elem.offsetParent ) {
        // 增加偏移量
        p += elem.offsetTop;

        // 遍历下一个父元素
        elem = elem.offsetParent;
    }
    return p;
}