package com.chuwa.accountservice.dao;

import com.chuwa.accountservice.model.PaymentMethod;
import com.chuwa.accountservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    List<PaymentMethod> findPaymentMethodsByUser(User user);
    Optional<PaymentMethod> findByPaymentMethodIdAndUser(Long paymentMethodId, User user);

}
