package com.chuwa.paymentservice.client;

import com.chuwa.paymentservice.payload.PaymentMethodDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "account-service", path = "/api/v1/user")
public interface AccountClient {

    @GetMapping("/payment-methods")
    PaymentMethodDTO getPaymentMethod(@RequestParam("paymentMethodId") Long paymentMethodId);


}
