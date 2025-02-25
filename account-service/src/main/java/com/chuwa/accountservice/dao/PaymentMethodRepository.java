package com.chuwa.accountservice.dao;

import com.chuwa.accountservice.model.PaymentMethod;
import com.chuwa.accountservice.model.User;
import com.chuwa.accountservice.model.compositekey.PaymentMethodId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, PaymentMethodId> {
    List<PaymentMethod> findPaymentMethodsByUser(User user);

}
