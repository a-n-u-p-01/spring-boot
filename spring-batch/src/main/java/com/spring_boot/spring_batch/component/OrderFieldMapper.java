package com.spring_boot.spring_batch.component;

import com.spring_boot.spring_batch.entity.Order;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class OrderFieldMapper implements FieldSetMapper<Order> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Order mapFieldSet(FieldSet fieldSet) throws BindException {
        Order order = new Order();
        order.setOrderRef(fieldSet.readString("order_ref"));
        order.setAmount(fieldSet.readString("amount"));
        return order;
    }
}
