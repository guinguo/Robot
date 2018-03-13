package top.guinguo.modules.weibo.task;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.NShort.NShortSegment;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.Viterbi.ViterbiSegment;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;
import com.hankcs.hanlp.tokenizer.SpeedTokenizer;
import org.apache.commons.lang.StringUtils;
import top.guinguo.modules.weibo.dao.HBaseDaoImlp;
import top.guinguo.modules.weibo.model.Label;
import top.guinguo.modules.weibo.model.MidleWeibo;
import top.guinguo.modules.weibo.model.Task;
import top.guinguo.modules.weibo.model.Weibo;
import top.guinguo.modules.weibo.service.IWeiboService;
import top.guinguo.modules.weibo.service.WeiboService;
import top.guinguo.modules.weibo.utils.Contants;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by guin_guo on 2018/3/10.
 */
public class TaskManager {
    private int initPoolSize = 12;
    private ExecutorService fixedThreadPool;
    private HBaseDaoImlp hBaseDaoImlp;
    private IWeiboService weiboService;

    public TaskManager(int initPoolSize) {
        this.initPoolSize = initPoolSize;
        this.hBaseDaoImlp = HBaseDaoImlp.getInstance();
        this.weiboService= WeiboService.getInstance();
        fixedThreadPool = Executors.newFixedThreadPool(initPoolSize);
    }
    private static class DefaultInstance {
        public static TaskManager instance = new TaskManager(12);
    }
    public static TaskManager getInstance() {
        return DefaultInstance.instance;
    }

    public void runTask(Task task) {
        System.out.println("running task: " + task);
        fixedThreadPool.submit(new TaskRunner(task));
    }

    class TaskRunner implements Runnable {
        private Task task;
        public TaskRunner(Task task) {
            this.task = task;
        }
        @Override
        public void run() {
            List<Weibo> weibos;
            try {
                weibos = weiboService.getWeiboByUid(task.getUserid());
                Map<String, List<MidleWeibo>> top5 = getTop5(weibos);
                List<String> worldCloud = getWordCloud(weibos);
                List<Label> getUserLabels = getUserLabels(weibos);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * top 5
         * @param weiboList
         * @return
         */
        private Map<String, List<MidleWeibo>> getTop5(List<Weibo> weiboList) {
            Collections.reverse(weiboList);
            List<MidleWeibo> latest = new ArrayList<>(5);
            //latest 5
            for (int i=0;i<5;i++) {
                Weibo weibo = weiboList.get(i);
                MidleWeibo midleWeibo = new MidleWeibo(weibo.getContent(), weibo.getCommentNumber().intValue(), weibo.getCreateDate());
                latest.add(midleWeibo);
            }
            //mostComments 5
            Collections.sort(weiboList, (o1, o2) -> o2.getCommentNumber().compareTo(o1.getCommentNumber()));
            List<MidleWeibo> mostComments = new ArrayList<>(5);
            for (int i=0;i<5;i++) {
                Weibo weibo = weiboList.get(i);
                MidleWeibo midleWeibo = new MidleWeibo(weibo.getContent(), weibo.getCommentNumber().intValue(), weibo.getCreateDate());
                mostComments.add(midleWeibo);
            }
            //mostForwards
            Collections.sort(weiboList, (o1, o2) -> o2.getForwardNumber().compareTo(o1.getForwardNumber()));
            List<MidleWeibo> mostForwards = new ArrayList<>(5);
            for (int i=0;i<5;i++) {
                Weibo weibo = weiboList.get(i);
                MidleWeibo midleWeibo = new MidleWeibo(weibo.getContent(), weibo.getForwardNumber().intValue());
                mostForwards.add(midleWeibo);
            }
            //mostLikes
            Collections.sort(weiboList, (o1, o2) -> o2.getLikeNumber().compareTo(o1.getLikeNumber()));
            List<MidleWeibo> mostLikes = new ArrayList<>(5);
            for (int i=0;i<5;i++) {
                Weibo weibo = weiboList.get(i);
                MidleWeibo midleWeibo = new MidleWeibo(weibo.getLikeNumber().intValue(), weibo.getContent());
                mostLikes.add(midleWeibo);
            }
            Map<String, List<MidleWeibo>> top5 = new HashMap<>(4);
            top5.put("latest", latest);
            top5.put("mostComments", mostComments);
            top5.put("mostForwards", mostForwards);
            top5.put("mostLikes", mostLikes);
            return top5;
        }

        /**
         * 所有微博的前十个关键词
         * @param weiboList
         * @return
         */
        private List<String> getWordCloud(List<Weibo> weiboList) {
            StringBuilder sb = new StringBuilder("");
            for (Weibo weibo : weiboList) {
                sb.append(weibo.getContent()).append("。");
            }
            List<String> kws2 = HanLP.extractKeyword(sb.toString(), 50);
            return kws2;
        }
        /**
         * 标签
         * @param weiboList
         * @return
         */
        private List<Label> getUserLabels(List<Weibo> weiboList) {
            Set<String> texts = new HashSet<>();
            Map<String, Integer> textMap = new HashMap<>();
            int sum = 0;
            for (Weibo weibo : weiboList) {
                String topics = weibo.getTopics();
                if (StringUtils.isNotEmpty(topics)) {
                    String[] ts = topics.split("#");
                    for (String t : ts) {
                        sum++;
                        texts.add(t);
                        if (textMap.get(t) != null) {
                            textMap.put(t, textMap.get(t) + 1);
                        } else {
                            textMap.put(t, 1);
                        }
                    }
                }
            }
            List<Map.Entry<String, Integer>> list = new ArrayList<>(textMap.entrySet());
            Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
            int i = 0;
            List<Label> labels = new ArrayList<>(10);
            for (Map.Entry<String, Integer> map : list) {
                Label label = new Label(map.getKey(), map.getValue() / sum * 1.0);
                labels.add(label);
                if (++i == 10) {
                    break;
                }
            }
            return labels;
        }
    }
}
