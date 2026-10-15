package com.MedLinks.MedLinks_10.Repository;

import com.MedLinks.MedLinks_10.Entities.Cart;
import com.MedLinks.MedLinks_10.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser(User user);
}