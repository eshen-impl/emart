package com.chuwa.paymentservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RefundRequestDTO {

    private UUID transactionKey;
    private BigDecimal requestRefundAmount;
}
