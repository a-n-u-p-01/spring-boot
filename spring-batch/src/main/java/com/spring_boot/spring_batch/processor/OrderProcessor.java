package com.spring_boot.spring_batch.processor;

import com.spring_boot.spring_batch.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;

@Slf4j
@Component
public class OrderProcessor implements ItemProcessor<Order,Order> {
    @Override
    public Order process(Order order) throws Exception {
        Order processedOrder = new Order();
        processedOrder.setOrderRef(order.getOrderRef());
        processedOrder.setAmount(order.getAmount());
        return processedOrder;
    }
}
