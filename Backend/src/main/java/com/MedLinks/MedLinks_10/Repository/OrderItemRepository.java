package com.MedLinks.MedLinks_10.Repository;

import com.MedLinks.MedLinks_10.Entities.Order;
import com.MedLinks.MedLinks_10.Entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder(Order order);
}