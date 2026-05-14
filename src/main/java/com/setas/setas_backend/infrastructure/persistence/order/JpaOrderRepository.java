package com.setas.setas_backend.infrastructure.persistence.order;

import com.setas.setas_backend.domain.model.Order;
import com.setas.setas_backend.domain.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface JpaOrderRepository extends JpaRepository<Order, Long> {

  @Query("SELECT o FROM Order o WHERE o.user.email = :userEmail")
  List<Order> findByUserEmail(@Param("userEmail") String userEmail);

  @Modifying
  @Transactional
  @Query("UPDATE Order o SET o.status = :status WHERE o.id = :orderId")
  void updateStatus(@Param("orderId") Long orderId, @Param("status") OrderStatus status);

  @Modifying
  @Transactional
  @Query("UPDATE Order o SET o.paymentIntentId = :paymentIntentId WHERE o.id = :orderId")
  void updatePaymentIntentId(@Param("orderId") Long orderId, @Param("paymentIntentId") String paymentIntentId);

}
