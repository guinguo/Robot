package top.guinguo.modules.weibo.utils;

/**
 * @描述:
 * @作者: guin_guo
 * @日期: 2017-07-08 18:33
 * @版本: v1.0
 */
public interface Contants {
    //table
    String T_USER = "user2";
    String T_WEIBO = "weibo2";
    String T_USER_OLD = "user";
    String T_WEIBO_OLD = "weibo";

    //columnFamily
    String COLUMN_BASIC = "basic";
    String COLUMN_OTHER = "other";

    //regex
    String REGEX_FANS   = "<script>FM.view\\(\\{\"ns\":\"\",\"domid\":\"Pl_Core_T8CustomTriColumn__3\",(.*?)\\}\\)?</script>";
    String REGEX_NICK   = "<script type=\"text/javascript\">\\s+var \\$CONFIG = \\{\\};([\\s\\S]*?)?</script>";
    String REGEX_MEMBER = "<script>FM.view\\(\\{\"ns\":\"pl.header.head.index\",(.*?)\\}\\)?</script>";
    String REGEX_INFO   = "<script>FM.view\\(\\{\"ns\":\"pl.content.homeFeed.index\",\"domid\":\"Pl_Core_UserInfo__6\",(.*?)\\}\\)?</script>";

    //each user stop time of radio
    double intervalRadio = 0.55;

}
