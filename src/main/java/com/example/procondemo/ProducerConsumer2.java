package com.example.procondemo;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * 阻塞队列实现生产者消费者
 * 阻塞队列自带阻塞属性，所以实现起来比较简单
 *         添加元素             取出元素
 *   阻塞    put                take
 *   超时    offer(timeout)     poll(timeout)
 *
 * 返回特定值 offer              poll
 * 抛出异常   add               remove
 */
@Slf4j
public class ProducerConsumer2 {

    private static final LinkedBlockingDeque queue = new LinkedBlockingDeque(10);

    static class ProducerThread extends Thread {
        @Override
        public void run() {
            int i = 0;
            while (i < 10) {
                i++;
                try {
                    Object o = new Object();
                    queue.put(o);
                    log.info("生产者：{} 生产了一个数据：{}", Thread.currentThread().getName(), o);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class ConsumerThread extends Thread {
        @Override
        public void run() {
            int i = 0;
            while (i < 10) {
                i++;
                try {
                    Object take = queue.take();
                    log.info("消费者：{} 消费了一个数据：{}", Thread.currentThread().getName(), take);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        new ProducerThread().start();
        new ConsumerThread().start();
        new ProducerThread().start();
        new ConsumerThread().start();
    }
}
