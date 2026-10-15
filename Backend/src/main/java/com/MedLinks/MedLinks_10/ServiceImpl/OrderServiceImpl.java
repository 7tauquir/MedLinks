package com.MedLinks.MedLinks_10.ServiceImpl;

import com.MedLinks.MedLinks_10.DTO.OrderItemResponse;
import com.MedLinks.MedLinks_10.DTO.OrderRequest;
import com.MedLinks.MedLinks_10.DTO.OrderResponse;
import com.MedLinks.MedLinks_10.Entities.*;
import com.MedLinks.MedLinks_10.Repository.*;
import com.MedLinks.MedLinks_10.Service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MedicineRepository medicineRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            MedicineRepository medicineRepository,
            UserRepository userRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.medicineRepository = medicineRepository;
        this.userRepository = userRepository;
    }

    // ==========================================
    // PLACE ORDER
    // ==========================================

    @Override
    @Transactional
    public OrderResponse placeOrder(
            OrderRequest request,
            String shopOwnerEmail
    ) {

        User shopOwner = getShopOwner(shopOwnerEmail);

        Cart cart = cartRepository.findByUser(shopOwner)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Cart not found"
                        )
                );

        if (cart.getItems() == null ||
                cart.getItems().isEmpty()) {

            throw new RuntimeException(
                    "Cart is empty"
            );
        }

        if (request.getDeliveryAddress() == null ||
                request.getDeliveryAddress().isBlank()) {

            throw new RuntimeException(
                    "Delivery address is required"
            );
        }

        // Create Order
        Order order = new Order();

        order.setShopOwner(shopOwner);
        order.setDeliveryAddress(
                request.getDeliveryAddress()
        );
        order.setStatus(OrderStatus.valueOf("PLACED"));

        BigDecimal totalAmount = BigDecimal.ZERO;

        // Process every CartItem
        for (CartItem cartItem : cart.getItems()) {

            Medicine medicine = cartItem.getMedicine();

            int requestedQuantity =
                    cartItem.getQuantity();

            // Check stock
            if (medicine.getStock() < requestedQuantity) {

                throw new RuntimeException(
                        "Insufficient stock for medicine: "
                                + medicine.getName()
                );
            }

            // Create OrderItem
            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setMedicine(medicine);
            orderItem.setQuantity(requestedQuantity);

            // Save price at time of order
            orderItem.setPrice(
                    medicine.getPrice()
            );

            order.getItems().add(orderItem);

            // Calculate total
            BigDecimal subtotal =
                    medicine.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            requestedQuantity
                                    )
                            );

            totalAmount = totalAmount.add(subtotal);

            // Reduce stock
            medicine.setStock(
                    medicine.getStock()
                            - requestedQuantity
            );

            medicineRepository.save(medicine);
        }

        order.setTotalAmount(totalAmount);

        // Save Order + OrderItems
        Order savedOrder =
                orderRepository.save(order);

        // Clear Cart
        cartItemRepository.deleteAll(
                cart.getItems()
        );

        cart.getItems().clear();

        cartRepository.save(cart);

        return convertToResponse(savedOrder);
    }

    // ==========================================
    // GET ORDER BY ID
    // ==========================================

    @Override
    public OrderResponse getOrderById(
            Long orderId,
            String userEmail
    ) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(
                        () -> new RuntimeException(
                                "User not found"
                        )
                );

        Order order = orderRepository.findById(orderId)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Order not found"
                        )
                );

        /*
         * Retailer can only see own order.
         *
         * Wholesaler can see orders
         * containing their medicines.
         */

        if (user.getRole() == Role.SHOP_OWNER) {

            if (!order.getShopOwner()
                    .getId()
                    .equals(user.getId())) {

                throw new RuntimeException(
                        "You cannot access this order"
                );
            }
        }

        return convertToResponse(order);
    }

    // ==========================================
    // SHOP OWNER ORDERS
    // ==========================================

    @Override
    public List<OrderResponse> getMyOrders(
            String shopOwnerEmail
    ) {

        User shopOwner =
                getShopOwner(shopOwnerEmail);

        return orderRepository
                .findByShopOwner(shopOwner)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // ==========================================
    // WHOLESALER ORDERS
    // ==========================================

    @Override
    public List<OrderResponse> getOrdersForWholesaler(
            String wholesalerEmail
    ) {

        User wholesaler =
                getWholesaler(wholesalerEmail);

        /*
         * V1 has only ONE wholesaler.
         *
         * Therefore we find all orders and
         * return orders containing medicines
         * belonging to this wholesaler.
         */

        return orderRepository.findAll()
                .stream()
                .filter(order ->
                        order.getItems()
                                .stream()
                                .anyMatch(item ->
                                        item.getMedicine()
                                                .getSeller()
                                                .getId()
                                                .equals(
                                                        wholesaler.getId()
                                                )
                                )
                )
                .map(this::convertToResponse)
                .toList();
    }

    // ==========================================
    // UPDATE ORDER STATUS
    // ==========================================

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(
            Long orderId,
            String status,
            String wholesalerEmail
    ) {

        User wholesaler =
                getWholesaler(wholesalerEmail);

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Order not found"
                        )
                );

        // Check order belongs to this wholesaler
        boolean belongsToWholesaler =
                order.getItems()
                        .stream()
                        .anyMatch(item ->
                                item.getMedicine()
                                        .getSeller()
                                        .getId()
                                        .equals(
                                                wholesaler.getId()
                                        )
                        );

        if (!belongsToWholesaler) {

            throw new RuntimeException(
                    "You cannot update this order"
            );
        }

        // Validate status
        if (!isValidStatus(status)) {

            throw new RuntimeException(
                    "Invalid order status"
            );
        }

        order.setStatus(
                OrderStatus.valueOf(status.toUpperCase())
        );

        Order updatedOrder =
                orderRepository.save(order);

        return convertToResponse(updatedOrder);
    }

    // ==========================================
    // CANCEL ORDER
    // ==========================================

    @Override
    @Transactional
    public void cancelOrder(
            Long orderId,
            String shopOwnerEmail
    ) {

        User shopOwner =
                getShopOwner(shopOwnerEmail);

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Order not found"
                        )
                );

        // Security check
        if (!order.getShopOwner()
                .getId()
                .equals(shopOwner.getId())) {

            throw new RuntimeException(
                    "You cannot cancel this order"
            );
        }

        // Only PLACED orders can be cancelled
        if (!order.getStatus()
                .equals(OrderStatus.PLACED)) {

            throw new RuntimeException(
                    "Order cannot be cancelled now"
            );
        }

        // Return stock
        for (OrderItem item : order.getItems()) {

            Medicine medicine =
                    item.getMedicine();

            medicine.setStock(
                    medicine.getStock()
                            + item.getQuantity()
            );

            medicineRepository.save(medicine);
        }

        order.setStatus(OrderStatus.valueOf("CANCELLED"));

        orderRepository.save(order);
    }

    // ==========================================
    // VALID STATUS
    // ==========================================

    private boolean isValidStatus(String status) {

        try {
            OrderStatus.valueOf(status.toUpperCase());
            return true;

        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // ==========================================
    // GET SHOP OWNER
    // ==========================================

    private User getShopOwner(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RuntimeException(
                                "User not found"
                        )
                );

        if (user.getRole() != Role.SHOP_OWNER) {

            throw new RuntimeException(
                    "Only shop owners can perform this action"
            );
        }

        return user;
    }

    // ==========================================
    // GET WHOLESALER
    // ==========================================

    private User getWholesaler(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RuntimeException(
                                "User not found"
                        )
                );

        if (user.getRole() != Role.WHOLESALER) {

            throw new RuntimeException(
                    "Only wholesaler can perform this action"
            );
        }

        return user;
    }

    // ==========================================
    // ENTITY → DTO
    // ==========================================

    private OrderResponse convertToResponse(
            Order order
    ) {

        OrderResponse response =
                new OrderResponse();

        response.setOrderId(order.getId());
        response.setTotalAmount(
                order.getTotalAmount()
        );
        response.setStatus(
                String.valueOf(order.getStatus())
        );
        response.setDeliveryAddress(
                order.getDeliveryAddress()
        );

        if (order.getShopOwner() != null) {

            response.setShopOwnerId(
                    order.getShopOwner().getId()
            );

            response.setShopOwnerName(
                    order.getShopOwner().getName()
            );
        }

        for (OrderItem item : order.getItems()) {

            OrderItemResponse itemResponse =
                    new OrderItemResponse();

            Medicine medicine =
                    item.getMedicine();

            itemResponse.setMedicineId(
                    medicine.getId()
            );

            itemResponse.setMedicineName(
                    medicine.getName()
            );

            itemResponse.setQuantity(
                    item.getQuantity()
            );

            itemResponse.setPrice(
                    item.getPrice()
            );

            BigDecimal subtotal =
                    item.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            item.getQuantity()
                                    )
                            );

            itemResponse.setSubtotal(subtotal);

            response.getItems()
                    .add(itemResponse);
        }

        return response;
    }
}