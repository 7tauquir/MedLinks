package com.MedLinks.MedLinks_10.Controller;

import com.MedLinks.MedLinks_10.DTO.OrderRequest;
import com.MedLinks.MedLinks_10.DTO.OrderResponse;
import com.MedLinks.MedLinks_10.Service.OrderService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ==========================================
    // PLACE ORDER
    // SHOP OWNER ONLY
    // ==========================================

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
            @RequestBody OrderRequest request,
            @RequestParam String shopOwnerEmail
    ) {

        OrderResponse response =
                orderService.placeOrder(
                        request,
                        shopOwnerEmail
                );

        return ResponseEntity.ok(response);
    }


    // ==========================================
    // GET MY ORDERS
    // SHOP OWNER ONLY
    // ==========================================

    @GetMapping("/my")
    public ResponseEntity<List<OrderResponse>> getMyOrders(
            @RequestParam String shopOwnerEmail
    ) {

        return ResponseEntity.ok(
                orderService.getMyOrders(shopOwnerEmail)
        );
    }


    // ==========================================
    // GET ORDERS FOR WHOLESALER
    // WHOLESALER ONLY
    // ==========================================

    @GetMapping("/wholesaler")
    public ResponseEntity<List<OrderResponse>> getOrdersForWholesaler(
            @RequestParam String wholesalerEmail
    ) {

        return ResponseEntity.ok(
                orderService.getOrdersForWholesaler(
                        wholesalerEmail
                )
        );
    }


    // ==========================================
    // GET ORDER BY ID
    // AUTHENTICATED USER
    // ==========================================

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long orderId,
            @RequestParam String userEmail
    ) {

        return ResponseEntity.ok(
                orderService.getOrderById(
                        orderId,
                        userEmail
                )
        );
    }


    // ==========================================
    // UPDATE ORDER STATUS
    // WHOLESALER ONLY
    // ==========================================

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status,
            @RequestParam String wholesalerEmail
    ) {

        return ResponseEntity.ok(
                orderService.updateOrderStatus(
                        orderId,
                        status,
                        wholesalerEmail
                )
        );
    }


    // ==========================================
    // CANCEL ORDER
    // SHOP OWNER ONLY
    // ==========================================

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(
            @PathVariable Long orderId,
            @RequestParam String shopOwnerEmail
    ) {

        orderService.cancelOrder(
                orderId,
                shopOwnerEmail
        );

        return ResponseEntity.ok(
                "Order cancelled successfully"
        );
    }
}