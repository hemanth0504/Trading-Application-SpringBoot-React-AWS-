package com.Hemanth.trading.request;

import com.Hemanth.trading.domain.OrderType;
import lombok.Data;


@Data
public class CreateOrderRequest {
    private String coinId;
    private double quantity;
    private OrderType orderType;
}
