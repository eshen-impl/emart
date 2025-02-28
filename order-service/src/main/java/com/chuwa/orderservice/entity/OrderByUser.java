package com.chuwa.orderservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;

import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;
import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED;
import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("orders_by_user")
public class OrderByUser {

    @PrimaryKeyColumn(name = "user_id", type = PARTITIONED)
    private UUID userId;

    @PrimaryKeyColumn(name = "created_at", type = CLUSTERED)
    private LocalDateTime createdAt;

    @Column("order_id")
    private UUID orderId;
    @Column("order_status")
    private String orderStatus;

    @Column("total_amount")
    private BigDecimal totalAmount;
    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Column("items")
    private String items;

    @Column("payment_status")
    private String paymentStatus;


}

