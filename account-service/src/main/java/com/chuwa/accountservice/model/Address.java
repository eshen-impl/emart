package com.chuwa.accountservice.model;

import com.chuwa.accountservice.model.compositekey.AddressId;
import com.chuwa.accountservice.model.enumtype.AddressType;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // For auto-generated sequential ID
    @EmbeddedId
    private AddressId id;

    @Column(nullable = false)
    private String street;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private String state;
    @Column(nullable = false)
    private String postalCode;
    @Column(nullable = false)
    private String country;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AddressType type;
    private boolean isDefault;

    @ManyToOne
    @MapsId("userId") // Ensures userId in AddressId is also a Foreign Key
    @JoinColumn(name = "user_id", nullable = false) // Defines actual column name in DB
    private User user;
}
