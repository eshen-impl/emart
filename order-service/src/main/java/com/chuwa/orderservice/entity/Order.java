package com.chuwa.orderservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("orders")
public class Order {

    @PrimaryKeyColumn(name = "order_id", type = PrimaryKeyType.PARTITIONED)
    private UUID orderId;
    @Column("user_id")
    private UUID userId;

    @Column("order_status")
    private String orderStatus;

    @Column("total_amount")
    private BigDecimal totalAmount;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Column("items")
    private String items; //List of items in JSON

    @Column("payment_status")
    private String paymentStatus;

    @Column("shipping_address")
    private String shippingAddress;  // Full address in JSON

    @Column("billing_address")
    private String billingAddress;  // Full address in JSON

    @Column("payment_method_id")
    private String paymentMethodId; // Reference to Payment Service


}
