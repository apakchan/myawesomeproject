package com.learn.mitprojecttest;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

// 事件类
class MyEvent {
    private final String message;

    MyEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

// 订阅者类
class MySubscriber {
    @Subscribe
    public void onEvent(MyEvent event) {
        System.out.println("Received event: " + event.getMessage());
    }
}

public class EventBusTest {
    public static void main(String[] args) {
        // 创建 EventBus 实例
        EventBus eventBus = new EventBus();

        // 注册订阅者
        eventBus.register(new MySubscriber());

        // 发布事件
        eventBus.post(new MyEvent("userId: 1, orderId: 1234"));
        eventBus.post(new MyEvent("userId: 2, orderId: 4567"));
    }
}
