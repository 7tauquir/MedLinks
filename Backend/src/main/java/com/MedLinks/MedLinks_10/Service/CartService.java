package com.MedLinks.MedLinks_10.Service;

import com.MedLinks.MedLinks_10.DTO.CartItemRequest;
import com.MedLinks.MedLinks_10.DTO.CartResponse;

public interface CartService {

    CartResponse getCart(String userEmail);

    CartResponse addToCart(
            CartItemRequest request,
            String userEmail
    );

    CartResponse updateCartItem(
            Long medicineId,
            Integer quantity,
            String userEmail
    );

    CartResponse removeFromCart(
            Long medicineId,
            String userEmail
    );

    void clearCart(String userEmail);
}