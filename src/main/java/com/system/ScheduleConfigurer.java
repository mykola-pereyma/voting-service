package com.system;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ScheduleConfigurer {

    @Value("${order.time.hour}")
    private int orderHour;

    @Value("${order.time.minute}")
    private int orderMinute;

    public int getOrderHour() {
        return orderHour;
    }

    public void setOrderHour(int orderHour) {
        this.orderHour = orderHour;
    }

    public int getOrderMinute() {
        return orderMinute;
    }

    public void setOrderMinute(int orderMinute) {
        this.orderMinute = orderMinute;
    }
}
