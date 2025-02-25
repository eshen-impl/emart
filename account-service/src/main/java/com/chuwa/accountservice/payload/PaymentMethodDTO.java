package com.chuwa.accountservice.payload;

import com.chuwa.accountservice.model.enumtype.PaymentType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodDTO {

    @NotBlank(message = "Payment Method ID is required")
    private Long paymentMethodId;
    @NotBlank(message = "Payment type is required")
    private PaymentType type;
    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "^[0-9]{16}$", message = "Invalid card number format, must be 16 digits")
    private String cardNumber;

    @NotBlank(message = "Name on card is required")
    @Size(max = 100, message = "Name on card cannot exceed 100 characters")
    private String nameOnCard;

    @NotBlank(message = "Expiration date is required")
    @FutureOrPresent(message = "Expiration date cannot be in the past")
    private Date expirationDate;
}
