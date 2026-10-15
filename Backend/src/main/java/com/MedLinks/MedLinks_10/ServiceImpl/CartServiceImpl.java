package com.MedLinks.MedLinks_10.ServiceImpl;

import com.MedLinks.MedLinks_10.DTO.CartItemRequest;
import com.MedLinks.MedLinks_10.DTO.CartItemResponse;
import com.MedLinks.MedLinks_10.DTO.CartResponse;
import com.MedLinks.MedLinks_10.Entities.Cart;
import com.MedLinks.MedLinks_10.Entities.CartItem;
import com.MedLinks.MedLinks_10.Entities.Medicine;
import com.MedLinks.MedLinks_10.Entities.Role;
import com.MedLinks.MedLinks_10.Entities.User;
import com.MedLinks.MedLinks_10.Repository.CartItemRepository;
import com.MedLinks.MedLinks_10.Repository.CartRepository;
import com.MedLinks.MedLinks_10.Repository.MedicineRepository;
import com.MedLinks.MedLinks_10.Repository.UserRepository;
import com.MedLinks.MedLinks_10.Service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MedicineRepository medicineRepository;
    private final UserRepository userRepository;

    public CartServiceImpl(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            MedicineRepository medicineRepository,
            UserRepository userRepository
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.medicineRepository = medicineRepository;
        this.userRepository = userRepository;
    }

    // ==========================================
    // GET CART
    // ==========================================

    @Override
    public CartResponse getCart(String userEmail) {

        User user = getShopOwner(userEmail);

        Cart cart = getOrCreateCart(user);

        return convertToResponse(cart);
    }

    // ==========================================
    // ADD TO CART
    // ==========================================

    @Override
    @Transactional
    public CartResponse addToCart(
            CartItemRequest request,
            String userEmail
    ) {

        User user = getShopOwner(userEmail);

        if (request.getQuantity() == null ||
                request.getQuantity() <= 0) {

            throw new RuntimeException(
                    "Quantity must be greater than 0"
            );
        }

        Medicine medicine = medicineRepository
                .findById(request.getMedicineId())
                .orElseThrow(
                        () -> new RuntimeException(
                                "Medicine not found"
                        )
                );

        // Check stock
        if (medicine.getStock() < request.getQuantity()) {

            throw new RuntimeException(
                    "Insufficient medicine stock"
            );
        }

        Cart cart = getOrCreateCart(user);

        CartItem cartItem = cartItemRepository
                .findByCartAndMedicine(cart, medicine)
                .orElse(null);

        if (cartItem != null) {

            int newQuantity =
                    cartItem.getQuantity()
                            + request.getQuantity();

            if (newQuantity > medicine.getStock()) {

                throw new RuntimeException(
                        "Requested quantity exceeds available stock"
                );
            }

            cartItem.setQuantity(newQuantity);

        } else {

            cartItem = new CartItem();

            cartItem.setCart(cart);
            cartItem.setMedicine(medicine);
            cartItem.setQuantity(request.getQuantity());

            cart.getItems().add(cartItem);
        }

        cartItemRepository.save(cartItem);

        return convertToResponse(cart);
    }

    // ==========================================
    // UPDATE CART ITEM
    // ==========================================

    @Override
    @Transactional
    public CartResponse updateCartItem(
            Long medicineId,
            Integer quantity,
            String userEmail
    ) {

        User user = getShopOwner(userEmail);

        if (quantity == null || quantity <= 0) {

            throw new RuntimeException(
                    "Quantity must be greater than 0"
            );
        }

        Cart cart = getOrCreateCart(user);

        Medicine medicine = medicineRepository
                .findById(medicineId)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Medicine not found"
                        )
                );

        if (quantity > medicine.getStock()) {

            throw new RuntimeException(
                    "Requested quantity exceeds available stock"
            );
        }

        CartItem cartItem = cartItemRepository
                .findByCartAndMedicine(cart, medicine)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Medicine not found in cart"
                        )
                );

        cartItem.setQuantity(quantity);

        cartItemRepository.save(cartItem);

        return convertToResponse(cart);
    }

    // ==========================================
    // REMOVE FROM CART
    // ==========================================

    @Override
    @Transactional
    public CartResponse removeFromCart(
            Long medicineId,
            String userEmail
    ) {

        User user = getShopOwner(userEmail);

        Cart cart = getOrCreateCart(user);

        Medicine medicine = medicineRepository
                .findById(medicineId)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Medicine not found"
                        )
                );

        CartItem cartItem = cartItemRepository
                .findByCartAndMedicine(cart, medicine)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Medicine not found in cart"
                        )
                );

        cartItemRepository.delete(cartItem);

        return convertToResponse(cart);
    }

    // ==========================================
    // CLEAR CART
    // ==========================================

    @Override
    @Transactional
    public void clearCart(String userEmail) {

        User user = getShopOwner(userEmail);

        Cart cart = getOrCreateCart(user);

        cartItemRepository.deleteAll(cart.getItems());

        cart.getItems().clear();

        cartRepository.save(cart);
    }

    // ==========================================
    // GET OR CREATE CART
    // ==========================================

    private Cart getOrCreateCart(User user) {

        return cartRepository.findByUser(user)
                .orElseGet(() -> {

                    Cart cart = new Cart();

                    cart.setUser(user);
                    cart.setItems(new ArrayList<>());

                    return cartRepository.save(cart);
                });
    }

    // ==========================================
    // CHECK SHOP OWNER
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
                    "Only shop owners can manage cart"
            );
        }

        return user;
    }

    // ==========================================
    // CONVERT ENTITY → DTO
    // ==========================================

    private CartResponse convertToResponse(Cart cart) {

        CartResponse response = new CartResponse();

        response.setCartId(cart.getId());

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem item : cart.getItems()) {

            CartItemResponse itemResponse =
                    new CartItemResponse();

            Medicine medicine = item.getMedicine();

            BigDecimal subtotal =
                    medicine.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            item.getQuantity()
                                    )
                            );

            itemResponse.setId(item.getId());

            itemResponse.setMedicineId(
                    medicine.getId()
            );

            itemResponse.setMedicineName(
                    medicine.getName()
            );

            itemResponse.setPrice(
                    medicine.getPrice()
            );

            itemResponse.setQuantity(
                    item.getQuantity()
            );

            itemResponse.setSubtotal(subtotal);

            response.getItems().add(itemResponse);

            totalAmount = totalAmount.add(subtotal);
        }

        response.setTotalAmount(totalAmount);

        return response;
    }
}