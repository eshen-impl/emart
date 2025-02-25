package com.chuwa.accountservice.model.compositekey;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Embeddable
public class PaymentMethodId implements Serializable {
    private UUID userId;
    private Long paymentMethodId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentMethodId that = (PaymentMethodId) o;
        return userId.equals(that.userId) && paymentMethodId.equals(that.paymentMethodId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, paymentMethodId);
    }
}
