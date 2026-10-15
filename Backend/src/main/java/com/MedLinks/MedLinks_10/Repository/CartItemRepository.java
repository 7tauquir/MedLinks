package com.MedLinks.MedLinks_10.Repository;

import com.MedLinks.MedLinks_10.Entities.Cart;
import com.MedLinks.MedLinks_10.Entities.CartItem;
import com.MedLinks.MedLinks_10.Entities.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartAndMedicine(
            Cart cart,
            Medicine medicine
    );
}