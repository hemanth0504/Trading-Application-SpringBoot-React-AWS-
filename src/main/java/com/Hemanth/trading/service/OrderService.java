package com.Hemanth.trading.service;

import com.Hemanth.trading.domain.OrderType;
import com.Hemanth.trading.model.Coin;
import com.Hemanth.trading.model.Order;
import com.Hemanth.trading.model.OrderItem;
import com.Hemanth.trading.model.User;

import java.util.List;

public interface OrderService {

    Order createOrder(User user, OrderItem orderItem, OrderType orderType);

    Order getOrderById(Long orderId);

    List<Order> getAllOrdersForUser(Long userId, String orderType,String assetSymbol);

    void cancelOrder(Long orderId);

//    Order buyAsset(CreateOrderRequest req, Long userId, String jwt) throws Exception;

    Order processOrder(Coin coin, double quantity, OrderType orderType, User user) throws Exception;

//    Order sellAsset(CreateOrderRequest req,Long userId,String jwt) throws Exception;


}
