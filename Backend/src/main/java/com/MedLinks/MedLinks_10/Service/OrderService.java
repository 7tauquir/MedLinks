package com.MedLinks.MedLinks_10.Service;

import com.MedLinks.MedLinks_10.DTO.OrderRequest;
import com.MedLinks.MedLinks_10.DTO.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse placeOrder(
            OrderRequest request,
            String shopOwnerEmail
    );

    OrderResponse getOrderById(
            Long orderId,
            String userEmail
    );

    List<OrderResponse> getMyOrders(
            String shopOwnerEmail
    );

    List<OrderResponse> getOrdersForWholesaler(
            String wholesalerEmail
    );

    OrderResponse updateOrderStatus(
            Long orderId,
            String status,
            String wholesalerEmail
    );

    void cancelOrder(
            Long orderId,
            String shopOwnerEmail
    );
}