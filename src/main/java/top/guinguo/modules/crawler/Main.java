package top.guinguo.modules.crawler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @描述: 主方法
 * 启动1个生产者线程，8个消费者线程。
 * @作者: guin_guo
 * @日期: 2017-04-14 11:02
 * @版本: v1.0
 */
public class Main {
    public static void main(String[] args) {
        BlockingQueue<Video> queue = new ArrayBlockingQueue<>(35);
        Producer producer = new Producer(queue);
        new Thread(producer, "生产者").start();
        for (int i = 0; i < 8; i++) {
            System.out.println("启动消费者线程：" + i);
            new Thread(new Comsumer(queue), "消费者" + i).start();
        }
    }
}
