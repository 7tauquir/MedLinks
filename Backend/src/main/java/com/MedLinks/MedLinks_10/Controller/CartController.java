package com.MedLinks.MedLinks_10.Controller;

import com.MedLinks.MedLinks_10.DTO.CartItemRequest;
import com.MedLinks.MedLinks_10.DTO.CartResponse;
import com.MedLinks.MedLinks_10.Service.CartService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // ==========================================
    // ADD TO CART
    // ==========================================

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(
            @RequestBody CartItemRequest request,
            @RequestParam String shopOwnerEmail
    ) {

        CartResponse cart = cartService.addToCart(
                request,
                shopOwnerEmail
        );

        return ResponseEntity.ok(cart);
    }


    // ==========================================
    // GET CART
    // ==========================================

    @GetMapping
    public ResponseEntity<CartResponse> getCart(
            @RequestParam String shopOwnerEmail
    ) {

        return ResponseEntity.ok(
                cartService.getCart(shopOwnerEmail)
        );
    }


    // ==========================================
    // UPDATE CART ITEM
    // ==========================================

    @PutMapping("/update/{medicineId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable Long medicineId,
            @RequestParam String shopOwnerEmail,
            @RequestParam Integer quantity
    ) {

        CartResponse cart = cartService.updateCartItem(
                medicineId,
                quantity,
                shopOwnerEmail
        );

        return ResponseEntity.ok(cart);
    }


    // ==========================================
    // REMOVE FROM CART
    // ==========================================

    @DeleteMapping("/remove/{medicineId}")
    public ResponseEntity<CartResponse> removeFromCart(
            @PathVariable Long medicineId,
            @RequestParam String shopOwnerEmail
    ) {

        CartResponse cart = cartService.removeFromCart(
                medicineId,
                shopOwnerEmail
        );

        return ResponseEntity.ok(cart);
    }


    // ==========================================
    // CLEAR CART
    // ==========================================

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(
            @RequestParam String shopOwnerEmail
    ) {

        cartService.clearCart(shopOwnerEmail);

        return ResponseEntity.ok(
                "Cart cleared successfully"
        );
    }
}