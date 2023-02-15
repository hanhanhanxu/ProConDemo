package com.example.procondemo;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

/**
 * wait notifyAll 实现生产者消费者
 * lock Condition的实现差不多
 */
@Slf4j
public class ProducerConsumer1 {

    public static void main(String[] args) {

        // 缓冲区最多能放多少元素
        int MAX_COUNT = 5;
        // 缓冲区，这里不必使用线程安全的集合，因为对集合的操作都是在锁内
        List<Object> list = new LinkedList<>();
        // 共用一把锁
        Object monitor = new Object();

        new Thread(new Producer(monitor, MAX_COUNT, list)).start();
        new Thread(new Producer(monitor, MAX_COUNT, list)).start();
        new Thread(new Consumer(monitor, list)).start();
        new Thread(new Consumer(monitor, list)).start();
    }
}

@Slf4j
class Producer implements Runnable {

    private Object monitor;
    private int maxCount;
    private List list;

    public Producer(Object monitor, int maxCount, List list) {
        this.monitor = monitor;
        this.maxCount = maxCount;
        this.list = list;
    }

    @Override
    public void run() {
        int i = 0;
        while (i < 10) {
            i++;
            synchronized (monitor) {
                // 每次抢到锁后先检查是否能生产，不能生产就wait
                while (list.size() >= maxCount) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // 生产
                Object o = new Object();
                list.add(o);
                log.info("生产者：{} 生产了数据：{}", Thread.currentThread().getName(), o);
                // 生产完成后，唤醒其他线程
                monitor.notifyAll();
            }
        }
    }
}

@Slf4j
class Consumer implements Runnable {

    private Object monitor;
    private List list;

    public Consumer(Object monitor, List list) {
        this.monitor = monitor;
        this.list = list;
    }

    @Override
    public void run() {
        int i = 0;
        while (i < 10) {
            i++;
            synchronized (monitor) {
                // 抢到锁后先检查是否能消费，不能消费就wait
                while (list.size() <= 0) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // 消费
                Object o = list.remove(list.size() - 1);
                log.info("消费者：{} 消费了数据：{}", Thread.currentThread().getName(), o);
                // 消费完成后，唤醒其他线程
                monitor.notifyAll();
            }
        }
    }
}
/**
 * 0、最外层while(true)的作用
 * 1、为什么要用while 而不是if
 * 2、为什么要用notifyAll 而不是notify
 */