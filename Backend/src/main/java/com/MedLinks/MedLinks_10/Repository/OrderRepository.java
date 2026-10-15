package com.MedLinks.MedLinks_10.Repository;

import com.MedLinks.MedLinks_10.Entities.Order;
import com.MedLinks.MedLinks_10.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByShopOwner(User shopOwner);

    List<Order> findDistinctByItems_Medicine_Seller(User seller);
}