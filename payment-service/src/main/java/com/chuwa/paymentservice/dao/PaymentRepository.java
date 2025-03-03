package com.chuwa.paymentservice.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import com.chuwa.paymentservice.entity.Payment;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByOrderId(UUID orderId);

    Optional<Payment> findByTransactionKey(String transactionKey);

}

