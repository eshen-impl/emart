package com.chuwa.accountservice.model;


import com.chuwa.accountservice.model.compositekey.PaymentMethodId;
import com.chuwa.accountservice.model.enumtype.PaymentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payment_methods")
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EmbeddedId
    private PaymentMethodId id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType type;

    @Column(nullable = false)
    private String cardNumber;

    @Column(nullable = false)
    private String nameOnCard;

    @Column(nullable = false)
    private Date expirationDate;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
