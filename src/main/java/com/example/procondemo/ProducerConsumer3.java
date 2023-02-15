package com.example.procondemo;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

/**
 * @author: HanXu
 * on 2022/2/15
 * Class description: 生产者消费者
 * 基于wait notifyAll的另一种写法
 */
public class ProducerConsumer3 {

    public static void main(String[] args) {
        Channel channel = new Channel(5);
        new Thread(new ProducerRunnable(channel)).start();
        new Thread(new ProducerRunnable(channel)).start();
        new Thread(new ConsumerRunnable(channel)).start();
        new Thread(new ConsumerRunnable(channel)).start();
    }
}

class Channel {

    private List list = new LinkedList();
    private int capacity;

    public Channel(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void put(Object o) {
        while (list.size() >= capacity) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        list.add(o);
        notifyAll();
    }

    public synchronized Object get() {
        while (list.size() <= 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Object o = list.remove(list.size() - 1);
        return o;
    }
}

@Slf4j
class ProducerRunnable implements Runnable {

    private Channel channel;

    public ProducerRunnable(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            Object o = new Object();
            channel.put(o);
            log.info("生产者：{} 生产了数据：{}", Thread.currentThread().getName(), o);
        }
    }
}

@Slf4j
class ConsumerRunnable implements Runnable {

    private Channel channel;

    public ConsumerRunnable(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            Object o = channel.get();
            log.info("消费者：{} 消费了一个数据：{}", Thread.currentThread().getName(), o);
        }
    }
}