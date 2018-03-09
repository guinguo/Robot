package top.guinguo.modules.weibo.utils;

import org.apache.hadoop.hbase.client.Put;
import top.guinguo.modules.weibo.model.User;
import top.guinguo.modules.weibo.model.Weibo;

import java.util.ArrayList;
import java.util.List;

/**
 * @描述:
 * @作者: guin_guo
 * @日期: 2017-07-08 18:31
 * @版本: v1.0
 */
public class HBaseUtils {

    public static Put GeneratePutByUser(User user) {
        Put put = new Put(user.getId().getBytes());
        if (user.getUsername() != null) {
            put.addColumn(Contants.COLUMN_BASIC.getBytes(), "username".getBytes(), user.getUsername().getBytes());
        }
        if (user.getNickname() != null) {
            put.addColumn(Contants.COLUMN_BASIC.getBytes(), "nickname".getBytes(), user.getNickname().getBytes());
        }
        if (user.getSex() != null) {
            put.addColumn(Contants.COLUMN_BASIC.getBytes(), "sex".getBytes(), user.getSex().getBytes());
        }
        if (user.getAvatar() != null) {
            put.addColumn(Contants.COLUMN_BASIC.getBytes(), "avatar".getBytes(), user.getAvatar().getBytes());
        }
        if (user.getIntro() != null) {
            put.addColumn(Contants.COLUMN_BASIC.getBytes(), "intro".getBytes(), user.getIntro().getBytes());
        }
        if (user.getSex() != null) {
            put.addColumn(Contants.COLUMN_BASIC.getBytes(), "focus".getBytes(), (user.getFocus() + "").getBytes());
        }
        if (user.getFans() != null) {
            put.addColumn(Contants.COLUMN_BASIC.getBytes(), "fans".getBytes(), (user.getFans() + "").getBytes());
        }
        if (user.getBlogNumber() != null) {
            put.addColumn(Contants.COLUMN_BASIC.getBytes(), "blogNumber".getBytes(), (user.getBlogNumber() + "").getBytes());
        }
        if (user.getLevel() != null) {
            put.addColumn(Contants.COLUMN_BASIC.getBytes(), "level".getBytes(), (user.getLevel() + "").getBytes());
        }
        if (user.getAddress() != null) {
            put.addColumn(Contants.COLUMN_BASIC.getBytes(), "address".getBytes(), (user.getAddress()).getBytes());
        }
        if (user.getBirthDate() != null) {
            put.addColumn(Contants.COLUMN_BASIC.getBytes(), "birthDate".getBytes(), (DateUtils.format(user.getBirthDate())).getBytes());
        }
        if (user.getCrawlDate() != null) {
            put.addColumn(Contants.COLUMN_BASIC.getBytes(), "crawlDate".getBytes(), (DateUtils.format(user.getCrawlDate())).getBytes());
        }
        if (user.getRegistedDate() != null) {
            put.addColumn(Contants.COLUMN_BASIC.getBytes(), "registedDate".getBytes(), user.getRegistedDate().getBytes());
        }
        if (user.getTags() != null) {
            put.addColumn(Contants.COLUMN_BASIC.getBytes(), "tags".getBytes(), (user.getTags()).getBytes());
        }
        if (user.getSchool() != null) {
            put.addColumn(Contants.COLUMN_BASIC.getBytes(), "school".getBytes(), (user.getSchool()).getBytes());
        }
        if (user.getCompany() != null) {
            put.addColumn(Contants.COLUMN_BASIC.getBytes(), "company".getBytes(), (user.getCompany()).getBytes());
        }
        put.addColumn(Contants.COLUMN_BASIC.getBytes(), "member".getBytes(), (user.getMember() + "").getBytes());
        if (user.getMeta() != null) {
            put.addColumn(Contants.COLUMN_BASIC.getBytes(), "meta".getBytes(), (user.getMeta()).getBytes());
        }
        return put;
    }


    public static Put GeneratePutByWeibo(Weibo weibo) {
        Put put = new Put((weibo.getUid() + "_" + weibo.getId()).getBytes());
        if (weibo.getUid() != null) {
            put.addColumn(Contants.COLUMN_BASIC.getBytes(), "uid".getBytes(), weibo.getUid().getBytes());
        }
        if (weibo.getForward() != null) {
            put.addColumn(Contants.COLUMN_BASIC.getBytes(), "forward".getBytes(), (weibo.getForward() + "").getBytes());
        }
        if (weibo.getForwardmid() != null) {
            put.addColumn(Contants.COLUMN_BASIC.getBytes(), "forwardmid".getBytes(), weibo.getForwardmid().getBytes());
        }
        if (weibo.getForwarduid() != null) {
            put.addColumn(Contants.COLUMN_BASIC.getBytes(), "forwarduid".getBytes(), weibo.getForwarduid().getBytes());
        }
        if (weibo.getNickname() != null) {
            put.addColumn(Contants.COLUMN_BASIC.getBytes(), "nickname".getBytes(), weibo.getNickname().getBytes());
        }
        if (weibo.getCreateDate() != null) {
            put.addColumn(Contants.COLUMN_BASIC.getBytes(), "createDate".getBytes(), weibo.getCreateDate().getBytes());
        }
        if (weibo.getSource() != null) {
            put.addColumn(Contants.COLUMN_BASIC.getBytes(), "source".getBytes(), (weibo.getSource()).getBytes());
        }
        if (weibo.getHtmlContent() != null) {
            put.addColumn(Contants.COLUMN_OTHER.getBytes(), "htmlContent".getBytes(), (weibo.getHtmlContent()).getBytes());
        }
        if (weibo.getContent() != null) {
            put.addColumn(Contants.COLUMN_BASIC.getBytes(), "content".getBytes(), (weibo.getContent()).getBytes());
        }
        if (weibo.getTopics() != null) {
            put.addColumn(Contants.COLUMN_BASIC.getBytes(), "topics".getBytes(), (weibo.getTopics()).getBytes());
        }
        /*if (weibo.getVideo_src() != null) {
            put.addColumn(Contants.COLUMN_BASIC.getBytes(), "video_src".getBytes(), (weibo.getVideo_src()).getBytes());
        }*/
        if (weibo.getForwardNumber() != null) {
            put.addColumn(Contants.COLUMN_BASIC.getBytes(), "forwardNumber".getBytes(), (weibo.getForwardNumber().toString()).getBytes());
        }
        if (weibo.getCommentNumber() != null) {
            put.addColumn(Contants.COLUMN_BASIC.getBytes(), "commentNumber".getBytes(), (weibo.getCommentNumber().toString()).getBytes());
        }
        if (weibo.getLikeNumber() != null) {
            put.addColumn(Contants.COLUMN_BASIC.getBytes(), "likeNumber".getBytes(), (weibo.getLikeNumber().toString()).getBytes());
        }
        if (weibo.getCommentUrl() != null) {
            put.addColumn(Contants.COLUMN_BASIC.getBytes(), "commentUrl".getBytes(), (weibo.getCommentUrl()).getBytes());
        }
        /*if (weibo.getForwardUrl() != null) {
            put.addColumn(Contants.COLUMN_BASIC.getBytes(), "forwardUrl".getBytes(), (weibo.getForwardUrl()).getBytes());
        }*/
        if (weibo.getPicids() != null) {
            put.addColumn(Contants.COLUMN_BASIC.getBytes(), "picids".getBytes(), (weibo.getPicids()).getBytes());
        }
        if (weibo.getMeta() != null) {
            put.addColumn(Contants.COLUMN_OTHER.getBytes(), "meta".getBytes(), (weibo.getMeta()).getBytes());
        }
        return put;
    }

    public static List<Put> GeneratePutSByWeibo(List<Weibo> weibos) {
        List<Put> list = new ArrayList<>(weibos.size());
        for (Weibo weibo : weibos) {
            Put put = GeneratePutByWeibo(weibo);
            list.add(put);
        }
        return list;
    }
}
