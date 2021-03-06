package test.netease;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 正则匹配：

 排除
 1 首页一个很长的js：<script>\s(.*)\s+?</script>

 获取
 1 id昵称等: <script type="text/javascript">\s+var \$CONFIG = \{\};([\s\S]*?)?</script>
 <script>FM.view\(\{"ns":"pl.header.head.index",(.*?)\}\)?</script>
 2 粉丝数微博数等: <script>FM.view\(\{"ns":"","domid":"Pl_Core_T8CustomTriColumn__3",(.*?)\}\)?</script>
 3 个人信息等: <script>FM.view\(\{"ns":"pl.content.homeFeed.index","domid":"Pl_Core_UserInfo__6",(.*?)\}\)?</script>

 必要时删除\r\n \t\t等字符
 * @描述:
 * @作者: gzguoguinan
 * @日期: 2017-06-27 14:53
 * @版本: v1.0
 */
public class Test {
    public static void main(String[] args) {
        StringBuffer html = new StringBuffer().append("<script type=\"text/javascript\">\n" +
                "        var $CONFIG = {};\n" +
                "        $CONFIG['islogin']='2';\n" +
                "        $CONFIG['oid']='5652557382';\n" +
                "        $CONFIG['page_id']='1005055652557382';\n" +
                "        $CONFIG['onick']='---装逼卖萌无所不能---';\n" +
                "        $CONFIG['skin']='skin048';\n" +
                "        $CONFIG['background']='';\n" +
                "        $CONFIG['scheme']='';\n" +
                "        $CONFIG['colors_type']='';\n" +
                "        $CONFIG['uid']='3655689037';\n" +
                "        $CONFIG['nick']='欢迎新用户';\n" +
                "        $CONFIG['sex']='f';\n" +
                "        $CONFIG['watermark']='u/3655689037';\n" +
                "        $CONFIG['domain']='100505';\n" +
                "        $CONFIG['lang']='zh-cn';\n" +
                "        $CONFIG['avatar_large']='http://tva1.sinaimg.cn/crop.0.0.179.179.180/d9e5634djw1east9pi6bej2050050dfw.jpg';\n" +
                "        $CONFIG['timeDiff']=(new Date() - 1498543872000);\n" +
                "        $CONFIG['servertime']='1498543872';\n" +
                "        $CONFIG['location']='page_100505_home';\n" +
                "        $CONFIG['pageid']='';\n" +
                "        $CONFIG['title_value']='---装逼卖萌无所不能---的微博_微博';\n" +
                "        $CONFIG['$webim']='1';\n" +
                "        $CONFIG['miyou']='1';\n" +
                "        $CONFIG['brand']='0';\n" +
                "        $CONFIG['bigpipe']='true';\n" +
                "        $CONFIG['bpType']='page';\n" +
                "        $CONFIG['cssPath']='http://img.t.sinajs.cn/t6/';\n" +
                "        $CONFIG['imgPath']='http://img.t.sinajs.cn/t6/';\n" +
                "        $CONFIG['jsPath']='http://js.t.sinajs.cn/t6/';\n" +
                "        $CONFIG['mJsPath']=[\"http:\\/\\/js{n}.t.sinajs.cn\\/t6\\/\",1,2];\n" +
                "        $CONFIG['mCssPath']=[\"http:\\/\\/img{n}.t.sinajs.cn\\/t6\\/\",1,2];\n" +
                "        $CONFIG['version']='bcb788c21c3b2b7a';\n" +
                "        $CONFIG['g_mathematician']='1';\n" +
                "        $CONFIG['isAuto']='0';\n" +
                "        $CONFIG['timeweibo']='0';\n" +
                "        $CONFIG['pid']='100505';\n" +
                "    </script>" +
                "<script>FM.view({\"ns\":\"pl.content.homeFeed.index\",\"domid\":\"Pl_Core_UserInfo__6\",\"css\":[\"style/css/module/pagecard/PCD_person_info.css?version=63e1665afe69dee4\"],\"html\":\"<div class=\\\"WB_cardwrap S_bg2\\\" fixed-inbox=\\\"true\\\" node-type=\\\"sigleProfileUsrinfo\\\" fixed-mutex=\\\"false\\\">\\r\\n    <!-- v6 card 通用标题 -->\\r\\n\\t<div class=\\\"PCD_person_info\\\">\\r\\n\\t\\t\\t\\t<div class=\\\"verify_area W_tog_hover S_line2\\\">\\r\\n\\t\\t<p class=\\\"verify clearfix\\\">\\n\\t\\t\\t\\t\\t\\t\\t\\t<span class=\\\"icon_bed W_fl\\\"><a  suda-data=\\\"key=pc_apply_entry&value=profile_icon\\\" target=\\\"_blank\\\" href=\\\"http:\\/\\/verified.weibo.com\\/verify?from=profile\\\" class=\\\"W_icon icon_verify_v\\\"><\\/a><\\/span>\\n\\t\\t\\t\\t\\t\\t\\t\\t<span class=\\\"icon_group S_line1 W_fl\\\"><a class=\\\"W_icon_level icon_level_c2\\\" title=\\\"微博等级4\\\" href=\\\"http:\\/\\/level.account.weibo.com\\/level\\/levelexplain?from=profile2\\\" target=\\\"_black\\\"><span>Lv.4<\\/span><\\/a>&nbsp;<\\/span>\\n\\t\\t\\t\\t\\t\\t\\t<\\/p>\\t\\t \\t\\t \\t<p class=\\\"info\\\"><span>德国籍足球运动员托马斯穆勒，现效力于拜仁慕尼黑足球俱乐部<\\/span><\\/p>\\r\\n\\t\\t\\t<\\/div>\\r\\n\\t\\t\\r\\n\\t\\t<div class=\\\"WB_innerwrap\\\">\\r\\n\\t\\t\\t<div class=\\\"m_wrap\\\">\\r\\n\\t\\t\\t\\t\\t\\t\\t<div class=\\\"detail\\\">\\r\\n\\t\\t\\t\\t\\t<ul class=\\\"ul_detail\\\">\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t<li class=\\\"item S_line2 clearfix\\\">\\r\\n\\t\\t\\t\\t\\t\\t        \\t\\t\\t\\t\\t\\t<span class=\\\"item_ico W_fl\\\"><em class=\\\"W_ficon ficon_cd_place S_ficon\\\">2<\\/em><\\/span>\\r\\n    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t<span class=\\\"item_text W_fl\\\">\\r\\n    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t\\t海外 德国    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t<\\/span>\\r\\n\\t\\t\\t\\t\\t\\t<\\/li>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t<li class=\\\"item S_line2 clearfix\\\">\\r\\n\\t\\t\\t\\t\\t\\t        \\t\\t\\t\\t\\t\\t<span class=\\\"item_ico W_fl\\\"><em class=\\\"W_ficon ficon_constellation S_ficon\\\">ö<\\/em><\\/span>\\r\\n    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t<span class=\\\"item_text W_fl\\\">\\r\\n    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t\\t1989年9月13日    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t<\\/span>\\r\\n\\t\\t\\t\\t\\t\\t<\\/li>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t<li class=\\\"item S_line2 clearfix\\\">\\r\\n\\t\\t\\t\\t\\t\\t        \\t\\t\\t\\t\\t\\t<span class=\\\"item_ico W_fl\\\"><em class=\\\"W_ficon ficon_link S_ficon\\\">5<\\/em><\\/span>\\r\\n    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t<span class=\\\"item_text W_fl\\\">\\r\\n    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t个性域名：    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t\\t<a target=\\\"_blank\\\" href=\\\"http:\\/\\/weibo.com\\/thomasmueller25?from=profile&wvr=6\\\">thomasmueller25<\\/a>\\r\\n    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t<\\/span>\\r\\n\\t\\t\\t\\t\\t\\t<\\/li>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t<li class=\\\"item S_line2 clearfix\\\">\\r\\n\\t\\t\\t\\t\\t\\t        \\t\\t\\t\\t\\t\\t<span class=\\\"item_ico W_fl\\\"><em class=\\\"W_ficon ficon_cd_coupon S_ficon\\\">T<\\/em><\\/span>\\r\\n    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t<span class=\\\"item_text W_fl\\\">\\r\\n    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t<span class=\\\"S_txt2\\\">标签<\\/span>\\r\\n    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t\\t<a target=\\\"_blank\\\" href=\\\"http:\\/\\/s.weibo.com\\/user\\/&tag=%E4%BD%93%E8%82%B2%E8%B5%84%E8%AE%AF&from=profile&wvr=6\\\">体育资讯<\\/a>\\r\\n    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t<\\/span>\\r\\n\\t\\t\\t\\t\\t\\t<\\/li>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<\\/ul>\\r\\n\\t\\t\\t\\t<\\/div>\\r\\n\\t\\t\\t\\t\\t\\t<\\/div>\\r\\n\\t\\t<\\/div>\\r\\n\\t\\t<a class=\\\"WB_cardmore S_txt1 S_line1 clearfix\\\" \\r\\n    href=\\\"javascript:;\\\" action-type=\\\"login\\\"\\r\\n\\t>\\r\\n\\t<span class=\\\"more_txt\\\">查看更多&nbsp;<em class=\\\"W_ficon ficon_arrow_right S_ficon\\\">a<\\/em><\\/span>\\r\\n<\\/a>\\r\\n\\t\\t\\r\\n\\t<\\/div>\\r\\n<\\/div>\\r\\n\"})</script>"+
                "<script>FM.view({\"ns\":\"pl.header.head.index\",\"domid\":\"Pl_Official_Headerv6__1\",\"css\":[],\"js\":\"page/js/pl/header/head/index.js?version=bcb788c21c3b2b7a\",\"html\":\"<div class=\\\"PCD_header\\\">\\r\\n\\t\\t\\t\\t<div class=\\\"pf_wrap\\\" layout-shell=\\\"false\\\" node-type=\\\"cover_wrap\\\">\\r\\n\\t\\t\\t\\t<div class=\\\"cover_wrap\\\" node-type=\\\"cover\\\"  style=\\\"background-image:url(http:\\/\\/ww2.sinaimg.cn\\/crop.0.0.920.300\\/006axyopjw1exn12v9q9bj30pk08cacl.jpg)\\\"  cover-type=\\\"2\\\">\\r\\n\\t\\t\\t\\t<\\/div>\\r\\n\\t\\t\\t\\t<div class=\\\"shadow  S_shadow\\\" layout-shell=\\\"false\\\">\\r\\n\\t\\t\\t\\t\\t\\t<div class=\\\"pf_photo\\\" node-type=\\\"photo\\\">\\r\\n\\t\\t\\t\\t\\t\\t<p class=\\\"photo_wrap\\\">\\r\\n\\t\\t\\t\\t\\t\\t \\t\\t\\t\\t\\t\\t \\t<img src=\\\"http:\\/\\/tvax4.sinaimg.cn\\/crop.0.0.320.320.180\\/006axyoply1ffpvqmrm99j308w08w74k.jpg\\\" alt=\\\"托马斯穆勒ThomasMueller\\\" class=\\\"photo\\\">\\r\\n\\t\\t\\t\\t\\t\\t \\t\\t\\t\\t\\t\\t\\r\\n\\t\\t\\t\\t\\t\\t<\\/p>\\r\\n\\t\\t\\t\\t\\t\\t\\t<a suda-data=\\\"key=pc_apply_entry&value=profile_icon_avatar\\\"  href=\\\"http:\\/\\/verified.weibo.com\\/verify\\\" class=\\\"icon_bed\\\"><em title=\\\"德国籍足球运动员托马斯穆勒，现效力于拜仁慕尼黑足球俱乐部\\\" class=\\\"W_icon icon_pf_approve\\\" suda-uatrack=\\\"key=profile_head&value=vuser_guest\\\"><\\/em><\\/a>\\t\\t\\t\\t\\t\\t<\\/div>\\r\\n\\t\\t\\t\\t\\t\\t\\r\\n\\t\\t\\t\\t\\t\\t<div class=\\\"pf_username\\\">\\r\\n\\t\\t\\t\\t\\t\\t<h1 class=\\\"username\\\">托马斯穆勒ThomasMueller<\\/h1>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<span class=\\\"icon_bed\\\"><a><i class=\\\"W_icon icon_pf_male\\\"><\\/i><\\/a><\\/span>\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<a  title=\\\"微博会员\\\" target=\\\"_blank\\\" href=\\\"http:\\/\\/vip.weibo.com\\/personal?from=main\\\" action-type=\\\"ignore_list\\\"suda-uatrack=\\\"key=profile_head&value=member_guest\\\"><em class=\\\"W_icon icon_member5\\\"><\\/em><\\/a>\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\r\\n\\t\\t\\t\\t\\t\\t<\\/div>\\r\\n\\t\\t\\t\\t\\t\\t\\r\\n\\t\\t\\t\\t\\t\\t\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<div class=\\\"pf_intro\\\" title=\\\"德国籍足球运动员托马斯穆勒，现效力于拜仁慕尼黑足球俱乐部\\\">\\r\\n\\t\\t\\t\\t\\t\\t德国籍足球运动员托马斯穆勒，现效力于拜仁慕尼黑足球俱乐部\\t\\t\\t\\t\\t\\t<\\/div>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<div class=\\\"pf_opt\\\" diss-data=\\\"wforce=1&refer_sort=profile&refer_flag=profile_head\\\">\\r\\n\\t\\t\\t\\t\\t\\t\\t<div class=\\\"opt_box clearfix\\\">\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t<div class=\\\"btn_bed W_fl\\\" node-type=\\\"focusLink\\\" action-data=\\\"uid=5652557385&fnick=托马斯穆勒ThomasMueller&f=1&refer_flag=1005050001_&refer_lflag=&refer_from=profile_headerv6\\\">\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<a href=\\\"javascript:void(0);\\\" suda-uatrack=\\\"key=tblog_profile_v6&value=atten\\\" suda-data=\\\"key=tblog_attention_click&value=5652557385\\\"  action-type=\\\"login\\\"    class=\\\"W_btn_c btn_34px\\\"><em class=\\\"W_ficon ficon_add S_ficon\\\">+<\\/em>关注<\\/a>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<\\/div>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t<div class=\\\"btn_bed W_fl\\\"><a href=\\\"javascript:;\\\"  action-type=\\\"login\\\"  action-data=\\\"uid=5652557385&nick=托马斯穆勒ThomasMueller\\\" class=\\\"W_btn_d btn_34px\\\" suda-uatrack=\\\"key=tblog_profile_v6&value=private_letter\\\">私信<\\/a><\\/div>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t<div class=\\\"btn_bed W_fl\\\"><a href=\\\"javascript:;\\\" node-type=\\\"more\\\" class=\\\"W_btn_d W_btn_pf_menu btn_34px\\\"><em class=\\\"W_ficon ficon_menu S_ficon\\\">=<\\/em><\\/a><\\/div>\\r\\n\\t\\t\\t\\t\\t\\t\\t<\\/div>\\r\\n\\t\\t\\t\\t\\t\\t<\\/div>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<\\/div>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<a href=\\\"javascript:void(0);\\\" suda-uatrack=\\\"key=tblog_profile_new&value=icon_copycover\\\" class=\\\"pf_copy_icon\\\" action-data=\\\"copy=1&owner_uid=5652557385&type=custom&picid=006axyopjw1exn12v9q9bj30pk08cacl&coordinates=0,0|920,300\\\"  action-type=\\\"login\\\"  title=\\\"我也要用\\\"><\\/a>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<\\/div>\\r\\n\\t\\t\\t<\\/div>\\r\\n<div node-type=\\\"moreList\\\"  class=\\\"layer_menu_list_b\\\" style=\\\"position:absolute; top:332px; left:900px; z-index:999;display: none;\\\">\\t\\r\\n\\t<div class=\\\"list_wrap\\\">\\r\\n\\t\\t<div class=\\\"list_content W_f14\\\">\\r\\n  \\t\\t\\t<ul class=\\\"list_ul\\\">\\r\\n  \\t\\t\\t  \\t\\t\\t    \\t\\t    \\t\\t\\t    \\t\\t\\t<li class=\\\"item\\\"><a suda-data=\\\"key=tblog_otherprofile_v5&value=whisper\\\" href=\\\"javascript:void(0);\\\"  action-type=\\\"login\\\"  action-data=\\\"fuid=5652557385&fname=托马斯穆勒ThomasMueller&action=add\\\" class=\\\"tlink\\\" suda-uatrack=\\\"key=tblog_profile_v6&value=silently_concern\\\">悄悄关注<\\/a><\\/li>\\r\\n    \\t\\t\\t    \\t\\t    \\t\\t<li class=\\\"item\\\"><a action-data=\\\"title=%E6%8A%8A%E6%89%98%E9%A9%AC%E6%96%AF%E7%A9%86%E5%8B%92ThomasMueller%E6%8E%A8%E8%8D%90%E7%BB%99%E6%9C%8B%E5%8F%8B&content=%E5%BF%AB%E6%9D%A5%E7%9C%8B%E7%9C%8B%E6%89%98%E9%A9%AC%E6%96%AF%E7%A9%86%E5%8B%92ThomasMueller%20%E7%9A%84%E5%BE%AE%E5%8D%9Ahttp:\\/\\/weibo.com\\/thomasmueller25\\\" href=\\\"javascript:void(0);\\\"  action-type=\\\"login\\\"  class=\\\"tlink\\\" suda-uatrack=\\\"key=tblog_profile_v6&value=suggest_to_friends\\\">推荐给朋友<\\/a><\\/li>\\r\\n  \\t\\t\\t<\\/ul>\\r\\n  \\t\\t\\t\\t\\t\\t<ul class=\\\"list_ul\\\">\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<li class=\\\"item\\\"><a suda-data=\\\"key=tblog_otherprofile_v5&value=join_blacklist\\\" href=\\\"javascript:void(0);\\\"  action-type=\\\"login\\\"  class=\\\"tlink\\\" suda-uatrack=\\\"key=tblog_profile_v6&value=in_black_list\\\">加入黑名单<\\/a><\\/li>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t<li class=\\\"item\\\"><a suda-data=\\\"key=tblog_otherprofile_v5&value=report\\\" href=\\\"javascript:void(0);\\\"  action-type=\\\"login\\\"  class=\\\"tlink\\\" suda-uatrack=\\\"key=tblog_profile_v6&value=report\\\">举报他<\\/a><\\/li>\\r\\n  \\t\\t\\t  \\t\\t\\t<\\/ul>\\r\\n  \\t\\t\\t\\t\\t<\\/div>\\r\\n  <\\/div>\\r\\n<\\/div>\\r\\n\"})</script>\n" +
//                "<script>FM.view({\"ns\":\"pl.header.head.index\",\"domid\":\"Pl_Official_Headerv6__1\",\"css\":[],\"js\":\"page/js/pl/header/head/index.js?version=bcb788c21c3b2b7a\",\"html\":\"<div class=\\\"PCD_header\\\">\\r\\n\\t\\t\\t\\t<div class=\\\"pf_wrap\\\" layout-shell=\\\"false\\\" node-type=\\\"cover_wrap\\\">\\r\\n\\t\\t\\t\\t<div class=\\\"cover_wrap\\\" node-type=\\\"cover\\\"  style=\\\"background-image:url(http:\\/\\/img.t.sinajs.cn\\/t5\\/skin\\/public\\/profile_cover\\/012.jpg)\\\"  cover-type=\\\"1\\\">\\r\\n\\t\\t\\t\\t<\\/div>\\r\\n\\t\\t\\t\\t<div class=\\\"shadow  S_shadow\\\" layout-shell=\\\"false\\\">\\r\\n\\t\\t\\t\\t\\t\\t<div class=\\\"pf_photo\\\" node-type=\\\"photo\\\">\\r\\n\\t\\t\\t\\t\\t\\t<p class=\\\"photo_wrap\\\">\\r\\n\\t\\t\\t\\t\\t\\t \\t\\t\\t\\t\\t\\t \\t<img src=\\\"http:\\/\\/tva2.sinaimg.cn\\/crop.0.0.664.664.180\\/006axyomjw8fatvd19lyjj30ig0igwfr.jpg\\\" alt=\\\"---装逼卖萌无所不能---\\\" class=\\\"photo\\\">\\r\\n\\t\\t\\t\\t\\t\\t \\t\\t\\t\\t\\t\\t\\r\\n\\t\\t\\t\\t\\t\\t<\\/p>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<\\/div>\\r\\n\\t\\t\\t\\t\\t\\t\\r\\n\\t\\t\\t\\t\\t\\t<div class=\\\"pf_username\\\">\\r\\n\\t\\t\\t\\t\\t\\t<h1 class=\\\"username\\\">---装逼卖萌无所不能---<\\/h1>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<span class=\\\"icon_bed\\\"><a><i class=\\\"W_icon icon_pf_female\\\"><\\/i><\\/a><\\/span>\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\r\\n\\t\\t\\t\\t\\t\\t<\\/div>\\r\\n\\t\\t\\t\\t\\t\\t\\r\\n\\t\\t\\t\\t\\t\\t\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<div class=\\\"pf_intro\\\" >\\r\\n\\t\\t\\t\\t\\t\\t她还没有填写个人简介\\t\\t\\t\\t\\t\\t<\\/div>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<div class=\\\"pf_opt\\\" diss-data=\\\"wforce=1&refer_sort=profile&refer_flag=profile_head\\\">\\r\\n\\t\\t\\t\\t\\t\\t\\t<div class=\\\"opt_box clearfix\\\">\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t<div class=\\\"btn_bed W_fl\\\" node-type=\\\"focusLink\\\" action-data=\\\"uid=5652557382&fnick=---装逼卖萌无所不能---&f=1&refer_flag=1005050001_&refer_lflag=&refer_from=profile_headerv6\\\">\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<a href=\\\"javascript:void(0);\\\" suda-uatrack=\\\"key=tblog_profile_v6&value=atten\\\" suda-data=\\\"key=tblog_attention_click&value=5652557382\\\"  action-type=\\\"login\\\"    class=\\\"W_btn_c btn_34px\\\"><em class=\\\"W_ficon ficon_add S_ficon\\\">+<\\/em>关注<\\/a>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<\\/div>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t<div class=\\\"btn_bed W_fl\\\"><a href=\\\"javascript:;\\\"  action-type=\\\"login\\\"  action-data=\\\"uid=5652557382&nick=---装逼卖萌无所不能---\\\" class=\\\"W_btn_d btn_34px\\\" suda-uatrack=\\\"key=tblog_profile_v6&value=private_letter\\\">私信<\\/a><\\/div>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t<div class=\\\"btn_bed W_fl\\\"><a href=\\\"javascript:;\\\" node-type=\\\"more\\\" class=\\\"W_btn_d W_btn_pf_menu btn_34px\\\"><em class=\\\"W_ficon ficon_menu S_ficon\\\">=<\\/em><\\/a><\\/div>\\r\\n\\t\\t\\t\\t\\t\\t\\t<\\/div>\\r\\n\\t\\t\\t\\t\\t\\t<\\/div>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<\\/div>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<div class=\\\"pf_use_num\\\" node-type=\\\"use_num\\\">超过<span class=\\\"W_Tahoma W_fb\\\">1000<\\/span>万人正在使用<\\/div>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<a href=\\\"javascript:void(0);\\\" suda-uatrack=\\\"key=tblog_profile_new&value=icon_copycover\\\" class=\\\"pf_copy_icon\\\" action-data=\\\"copy=1&owner_uid=5652557382&type=system&cover_id=12\\\"  action-type=\\\"login\\\" title=\\\"我也要用\\\"><\\/a>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<\\/div>\\r\\n\\t\\t\\t<\\/div>\\r\\n<div node-type=\\\"moreList\\\"  class=\\\"layer_menu_list_b\\\" style=\\\"position:absolute; top:332px; left:900px; z-index:999;display: none;\\\">\\t\\r\\n\\t<div class=\\\"list_wrap\\\">\\r\\n\\t\\t<div class=\\\"list_content W_f14\\\">\\r\\n  \\t\\t\\t<ul class=\\\"list_ul\\\">\\r\\n  \\t\\t\\t  \\t\\t\\t    \\t\\t    \\t\\t\\t    \\t\\t\\t<li class=\\\"item\\\"><a suda-data=\\\"key=tblog_otherprofile_v5&value=whisper\\\" href=\\\"javascript:void(0);\\\"  action-type=\\\"login\\\"  action-data=\\\"fuid=5652557382&fname=---装逼卖萌无所不能---&action=add\\\" class=\\\"tlink\\\" suda-uatrack=\\\"key=tblog_profile_v6&value=silently_concern\\\">悄悄关注<\\/a><\\/li>\\r\\n    \\t\\t\\t    \\t\\t    \\t\\t<li class=\\\"item\\\"><a action-data=\\\"title=%E6%8A%8A---%E8%A3%85%E9%80%BC%E5%8D%96%E8%90%8C%E6%97%A0%E6%89%80%E4%B8%8D%E8%83%BD---%E6%8E%A8%E8%8D%90%E7%BB%99%E6%9C%8B%E5%8F%8B&content=%E5%BF%AB%E6%9D%A5%E7%9C%8B%E7%9C%8B---%E8%A3%85%E9%80%BC%E5%8D%96%E8%90%8C%E6%97%A0%E6%89%80%E4%B8%8D%E8%83%BD---%20%E7%9A%84%E5%BE%AE%E5%8D%9Ahttp:\\/\\/weibo.com\\/u\\/5652557382\\\" href=\\\"javascript:void(0);\\\"  action-type=\\\"login\\\"  class=\\\"tlink\\\" suda-uatrack=\\\"key=tblog_profile_v6&value=suggest_to_friends\\\">推荐给朋友<\\/a><\\/li>\\r\\n  \\t\\t\\t<\\/ul>\\r\\n  \\t\\t\\t\\t\\t\\t<ul class=\\\"list_ul\\\">\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<li class=\\\"item\\\"><a suda-data=\\\"key=tblog_otherprofile_v5&value=join_blacklist\\\" href=\\\"javascript:void(0);\\\"  action-type=\\\"login\\\"  class=\\\"tlink\\\" suda-uatrack=\\\"key=tblog_profile_v6&value=in_black_list\\\">加入黑名单<\\/a><\\/li>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t<li class=\\\"item\\\"><a suda-data=\\\"key=tblog_otherprofile_v5&value=report\\\" href=\\\"javascript:void(0);\\\"  action-type=\\\"login\\\"  class=\\\"tlink\\\" suda-uatrack=\\\"key=tblog_profile_v6&value=report\\\">举报她<\\/a><\\/li>\\r\\n  \\t\\t\\t  \\t\\t\\t<\\/ul>\\r\\n  \\t\\t\\t\\t\\t<\\/div>\\r\\n  <\\/div>\\r\\n<\\/div>\\r\\n\"})</script>"+
                "<script>FM.view({\"ns\":\"pl.nav.index\",\"domid\":\"Pl_Official_Nav__2\",\"css\":[],\"js\":\"page/js/pl/nav/index.js?version=bcb788c21c3b2b7a\",\"html\":\"<div class=\\\"PCD_tab S_bg2\\\">\\r\\n\\t<div class=\\\"tab_wrap\\\" style=\\\"width:60%\\\">\\r\\n\\t\\t<table class=\\\"tb_tab\\\" cellpadding=\\\"0\\\" cellspacing=\\\"0\\\">\\r\\n\\t\\t\\t<tr>\\r\\n\\t\\t\\t\\t\\t\\t\\t<td class=\\\"current\\\" >\\r\\n\\t\\t\\t\\t\\t<a  bpfilter=\\\"page\\\" href=\\\"\\/p\\/1005055652557385\\/home?from=page_100505&mod=TAB#place\\\" node-type=\\\"nav_link\\\" suda-uatrack=\\\"key=tblog_profile_new&value=tab_profile\\\" class=\\\"tab_link\\\">\\r\\n\\t\\t\\t\\t\\t\\t<span class=\\\"S_txt1 t_link\\\">他的主页<\\/span>\\r\\n\\t\\t\\t\\t\\t\\t<span class=\\\"ani_border\\\"><\\/span>\\r\\n\\t\\t\\t\\t\\t<\\/a>\\r\\n\\t\\t\\t\\t<\\/td>\\r\\n\\t\\t\\t\\t\\t\\t\\t<td  >\\r\\n\\t\\t\\t\\t\\t<a  bpfilter=\\\"page\\\" href=\\\"\\/p\\/1005055652557385\\/hotspot?from=page_100505&mod=TAB#place\\\" node-type=\\\"nav_link\\\" suda-uatrack=\\\"key=hotspot_profile&value=tab_hotspot\\\" class=\\\"tab_link\\\">\\r\\n\\t\\t\\t\\t\\t\\t<span class=\\\"S_txt1 t_link\\\">他的头条<\\/span>\\r\\n\\t\\t\\t\\t\\t\\t<span class=\\\"ani_border\\\"><\\/span>\\r\\n\\t\\t\\t\\t\\t<\\/a>\\r\\n\\t\\t\\t\\t<\\/td>\\r\\n\\t\\t\\t\\t\\t\\t\\t<td  >\\r\\n\\t\\t\\t\\t\\t<a  bpfilter=\\\"page\\\" href=\\\"\\/p\\/1005055652557385\\/photos?from=page_100505&mod=TAB#place\\\" node-type=\\\"nav_link\\\" suda-uatrack=\\\"key=tblog_profile_new&value=tab_photos\\\" class=\\\"tab_link\\\">\\r\\n\\t\\t\\t\\t\\t\\t<span class=\\\"S_txt1 t_link\\\">他的相册<\\/span>\\r\\n\\t\\t\\t\\t\\t\\t<span class=\\\"ani_border\\\"><\\/span>\\r\\n\\t\\t\\t\\t\\t<\\/a>\\r\\n\\t\\t\\t\\t<\\/td>\\r\\n\\t\\t\\t\\t\\t\\t<\\/tr>\\r\\n\\t\\t<\\/table>\\r\\n\\t<\\/div>\\r\\n<\\/div>\\r\\n\"})</script>" +
                "<script>FM.view({\"ns\":\"\",\"domid\":\"Pl_Core_T8CustomTriColumn__3\",\"css\":[\"style/css/module/pagecard/PCD_counter.css?version=63e1665afe69dee4\"],\"html\":\"\\t<div class=\\\"WB_cardwrap S_bg2\\\" >\\r\\n\\t\\t\\t\\t\\t\\t<div class=\\\"PCD_counter\\\">\\r\\n\\t\\t\\t\\t\\t\\t\\t<div class=\\\"WB_innerwrap\\\">\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t<table class=\\\"tb_counter\\\" cellpadding=\\\"0\\\" cellspacing=\\\"0\\\">\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t<tbody>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<tr>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<td class=\\\"S_line1\\\">\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<a bpfilter=\\\"page_frame\\\"  class=\\\"t_link S_txt1\\\" href=\\\"http:\\/\\/weibo.com\\/p\\/1005055652557385\\/follow?from=page_100505&wvr=6&mod=headfollow#place\\\" ><strong class=\\\"W_f16\\\">11<\\/strong><span class=\\\"S_txt2\\\">关注<\\/span><\\/a><\\/td>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<td class=\\\"S_line1\\\">\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<a bpfilter=\\\"page_frame\\\"  class=\\\"t_link S_txt1\\\" href=\\\"http:\\/\\/weibo.com\\/p\\/1005055652557385\\/follow?relate=fans&from=100505&wvr=6&mod=headfans&current=fans#place\\\" ><strong class=\\\"W_f16\\\">266580<\\/strong><span class=\\\"S_txt2\\\">粉丝<\\/span><\\/a><\\/td>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<td class=\\\"S_line1\\\">\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<a bpfilter=\\\"page_frame\\\"  class=\\\"t_link S_txt1\\\" href=\\\"http:\\/\\/weibo.com\\/p\\/1005055652557385\\/home?from=page_100505_profile&wvr=6&mod=data#place\\\" ><strong class=\\\"W_f16\\\">538<\\/strong><span class=\\\"S_txt2\\\">微博<\\/span><\\/a><\\/td>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<\\/tr>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t<\\/tbody>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t<\\/table>\\r\\n\\t\\t\\t\\t\\t\\t\\t<\\/div>\\r\\n\\t\\t\\t\\t\\t\\t<\\/div>\\r\\n\\t\\t\\t\\t\\t<\\/div>\\r\\n\"})</script>"+
//                "<script>FM.view({\"ns\":\"\",\"domid\":\"Pl_Core_T8CustomTriColumn__3\",\"css\":[\"style/css/module/pagecard/PCD_counter.css?version=63e1665afe69dee4\"],\"html\":\"\\t<div class=\\\"WB_cardwrap S_bg2\\\" >\\r\\n\\t\\t\\t\\t\\t\\t<div class=\\\"PCD_counter\\\">\\r\\n\\t\\t\\t\\t\\t\\t\\t<div class=\\\"WB_innerwrap\\\">\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t<table class=\\\"tb_counter\\\" cellpadding=\\\"0\\\" cellspacing=\\\"0\\\">\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t<tbody>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<tr>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<td class=\\\"S_line1\\\">\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<a bpfilter=\\\"page_frame\\\"  class=\\\"t_link S_txt1\\\" href=\\\"http:\\/\\/weibo.com\\/p\\/1005055652557382\\/follow?from=page_100505&wvr=6&mod=headfollow#place\\\" ><strong class=\\\"W_f18\\\">119<\\/strong><span class=\\\"S_txt2\\\">关注<\\/span><\\/a><\\/td>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<td class=\\\"S_line1\\\">\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<a bpfilter=\\\"page_frame\\\"  class=\\\"t_link S_txt1\\\" href=\\\"http:\\/\\/weibo.com\\/p\\/1005055652557382\\/follow?relate=fans&from=100505&wvr=6&mod=headfans&current=fans#place\\\" ><strong class=\\\"W_f18\\\">31<\\/strong><span class=\\\"S_txt2\\\">粉丝<\\/span><\\/a><\\/td>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<td class=\\\"S_line1\\\">\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<a bpfilter=\\\"page_frame\\\"  class=\\\"t_link S_txt1\\\" href=\\\"http:\\/\\/weibo.com\\/p\\/1005055652557382\\/home?from=page_100505_profile&wvr=6&mod=data#place\\\" ><strong class=\\\"W_f18\\\">4<\\/strong><span class=\\\"S_txt2\\\">微博<\\/span><\\/a><\\/td>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<\\/tr>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t<\\/tbody>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t<\\/table>\\r\\n\\t\\t\\t\\t\\t\\t\\t<\\/div>\\r\\n\\t\\t\\t\\t\\t\\t<\\/div>\\r\\n\\t\\t\\t\\t\\t<\\/div>\\r\\n\"})</script>"+
                "<script>FM.view({\"ns\":\"pl.content.homeFeed.index\",\"domid\":\"Pl_Core_UserInfo__6\",\"css\":[\"style/css/module/pagecard/PCD_person_info.css?version=63e1665afe69dee4\"],\"html\":\"<div class=\\\"WB_cardwrap S_bg2\\\" fixed-inbox=\\\"true\\\" node-type=\\\"sigleProfileUsrinfo\\\" fixed-mutex=\\\"false\\\">\\r\\n    <!-- v6 card 通用标题 -->\\r\\n\\t<div class=\\\"PCD_person_info\\\">\\r\\n\\t\\t\\t\\t\\r\\n\\t\\t<div class=\\\"WB_innerwrap\\\">\\r\\n\\t\\t\\t<div class=\\\"m_wrap\\\">\\r\\n\\t\\t\\t\\t\\t\\t\\t<div class=\\\"detail\\\">\\r\\n\\t\\t\\t\\t\\t<ul class=\\\"ul_detail\\\">\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<li class=\\\"item S_line2 clearfix\\\">\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t<span class=\\\"item_ico W_fl\\\"><em class=\\\"W_ficon ficon_starmark S_ficon\\\">Û<\\/em><\\/span>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t<span class=\\\"item_text W_fl\\\"><a class=\\\"W_icon_level icon_level_c2\\\" title=\\\"微博等级7\\\" href=\\\"http:\\/\\/level.account.weibo.com\\/level\\/levelexplain?from=profile2\\\" target=\\\"_black\\\"><span>Lv.7<\\/span><\\/a><\\/span>\\r\\n\\t\\t\\t\\t\\t\\t<\\/li>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t<li class=\\\"item S_line2 clearfix\\\">\\r\\n\\t\\t\\t\\t\\t\\t        \\t\\t\\t\\t\\t\\t<span class=\\\"item_ico W_fl\\\"><em class=\\\"W_ficon ficon_cd_place S_ficon\\\">2<\\/em><\\/span>\\r\\n    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t<span class=\\\"item_text W_fl\\\">\\r\\n    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t\\t其他    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t<\\/span>\\r\\n\\t\\t\\t\\t\\t\\t<\\/li>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t<li class=\\\"item S_line2 clearfix\\\">\\r\\n\\t\\t\\t\\t\\t\\t        \\t\\t\\t\\t\\t\\t<span class=\\\"item_ico W_fl\\\"><em class=\\\"W_ficon ficon_constellation S_ficon\\\">ö<\\/em><\\/span>\\r\\n    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t<span class=\\\"item_text W_fl\\\">\\r\\n    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t\\t2015年4月20日    \\t\\t\\t\\t\\t\\t    \\t\\t\\t\\t\\t\\t<\\/span>\\r\\n\\t\\t\\t\\t\\t\\t<\\/li>\\r\\n\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t\\t<\\/ul>\\r\\n\\t\\t\\t\\t<\\/div>\\r\\n\\t\\t\\t\\t\\t\\t<\\/div>\\r\\n\\t\\t<\\/div>\\r\\n\\t\\t<a class=\\\"WB_cardmore S_txt1 S_line1 clearfix\\\" \\r\\n    href=\\\"javascript:;\\\" action-type=\\\"login\\\"\\r\\n\\t>\\r\\n\\t<span class=\\\"more_txt\\\">查看更多&nbsp;<em class=\\\"W_ficon ficon_arrow_right S_ficon\\\">a<\\/em><\\/span>\\r\\n<\\/a>\\r\\n\\t\\t\\r\\n\\t<\\/div>\\r\\n<\\/div>\\r\\n\"})</script>") ;
        //1. id昵称等
        String regex = "<script type=\"text/javascript\">\\s+var \\$CONFIG = \\{\\};([\\s\\S]*?)?</script>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html.toString());
        String result = "";
        while (matcher.find()) {
            result= (matcher.group(1));
        }
//        System.out.println(result);
        String[] ss = result.split("\\n");
        for (String s : ss) {
            s = s.trim();
            if (!s.isEmpty()) {
                if (s.startsWith("$CONFIG['oid']")) {
                    System.out.println("oid: "+s.split("=")[1]);
                } else if(s.startsWith("$CONFIG['page_id']")) {
                    System.out.println("page_id: "+s.split("=")[1]);
                } else if(s.startsWith("$CONFIG['onick']")) {
                    System.out.println("昵称: "+s.split("=")[1]);
                } else if(s.startsWith("$CONFIG['uid']")) {
                    System.out.println("uid: "+s.split("=")[1]);
                }
            }
        }
        //1.2 性别是否会员
        regex = "<script>FM.view\\(\\{\"ns\":\"pl.header.head.index\",(.*?)\\}\\)?</script>";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(html.toString());
        result = "";
        while (matcher.find()) {
            result= (matcher.group(1));
        }
        JSONObject json = JSON.parseObject("{"+result+"}");
        String htmldata = json.getString("html");
        Document document = Jsoup.parse(htmldata);
        System.out.println(document);
        String photo = document.select("img[class=photo]").get(0).attr("src");
        System.out.println("头像：" + photo);
        Element div = document.select(".pf_username").get(0);
        String username = div.select(".username").get(0).html();
        String intro = document.select(".pf_intro").first().html();
        System.out.println("简介：" + intro);
        System.out.println("用户名：" + username);
        System.out.println("性别： " + (div.select("a").get(0).select("i").get(0).hasClass("icon_pf_male") ? "男" : "女"));
//      <a title="微博会员" target="_blank" href="http://vip.weibo.com/personal?from=main" action-type="ignore_list"
// suda-uatrack="key=profile_head&amp;value=member_guest"><em class="W_icon icon_member5"></em></a>
        Elements wbmenber = div.select("a[title=微博会员]");
        if (wbmenber.size() > 0) {
            Set<String> clzz = wbmenber.get(0).select("em").get(0).classNames();
            String menlevel = "";
            for (String c : clzz) {
                if (c.startsWith("icon_member")) {
                    menlevel = c.substring(11);
                }
            }
            System.out.println("微博会员：" + menlevel);
        }
        //2. 粉丝数等
        regex = "<script>FM.view\\(\\{\"ns\":\"\",\"domid\":\"Pl_Core_T8CustomTriColumn__3\",(.*?)\\}\\)?</script>";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(html.toString());
        result = "";
        while (matcher.find()) {
            result= (matcher.group(1));
        }
        json = JSON.parseObject("{"+result+"}");
        htmldata = json.getString("html");
//        System.out.println(htmldata);
//        System.out.println(htmldata.replaceAll("\\r\\n", "").replaceAll("\\t", ""));
        document = Jsoup.parse(htmldata);
//        System.out.println(document);
        Elements as = document.select(".t_link");
        int wbcount = 0;
        for (Element a : as) {
            Element cnd = a.select(".S_txt2").get(0);
            String cn = cnd.html();
            int number = Integer.parseInt(cnd.previousElementSibling().html());
            System.out.println(cn+": "+number);
            if (cn.equals("微博")) {
                wbcount = number;
            }
        }
        //3个人信息等
//        System.out.println(document);
        regex = "<script>FM.view\\(\\{\"ns\":\"pl.content.homeFeed.index\",\"domid\":\"Pl_Core_UserInfo__6\",(.*?)\\}\\)?</script>";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(html.toString());
        result = "";
        while (matcher.find()) {
            result= (matcher.group(1));
        }
        json = JSON.parseObject("{"+result+"}");
        htmldata = json.getString("html");
//        System.out.println(htmldata);
//        System.out.println(htmldata.replaceAll("\\r\\n", "").replaceAll("\\t", ""));
        document = Jsoup.parse(htmldata);
        as = document.select(".W_icon_level span");
        System.out.println("level: " + as.get(0).html().substring(3));
        as = document.select(".info span");
        if (as.size() > 0) {
            System.out.println("info: " + as.get(0).html());
        }
        System.out.println("place: "+document.select(".ficon_cd_place").get(0).parent().nextElementSibling().html());
        System.out.println("birthday: "+document.select(".ficon_constellation").get(0).parent().nextElementSibling().html());
        as = document.select(".ficon_link");
        if (as.size() > 0) {
            System.out.println("link: "+as.get(0).parent().nextElementSibling().html());
        }
        as = document.select(".ficon_cd_coupon");
        if (as.size() > 0) {
            System.out.println("tags: "+as.get(0).parent().nextElementSibling().select("a").get(0).html());
        }
    }
}
