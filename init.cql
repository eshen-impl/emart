-- create_order_schema.cql
CREATE KEYSPACE IF NOT EXISTS emart_order WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
USE emart_order;

CREATE TABLE IF NOT EXISTS orders (
                                      order_id UUID PRIMARY KEY,
                                      user_id UUID,
                                      order_status TEXT,
                                      total_amount DECIMAL,
                                      created_at TIMESTAMP,
                                      updated_at TIMESTAMP,
                                      items TEXT,
                                      payment_status TEXT,
                                      shipping_address TEXT,
                                      billing_address TEXT,
                                      payment_method TEXT,
                                      transaction_key UUID,
                                      currency TEXT,
                                      refund_status TEXT,
                                      refunded_amount DECIMAL
);

CREATE TABLE IF NOT EXISTS orders_by_user (
                                              user_id UUID,
                                              order_id UUID,
                                              order_status TEXT,
                                              total_amount DECIMAL,
                                              created_at TIMESTAMP,
                                              updated_at TIMESTAMP,
                                              items TEXT,
                                              payment_status TEXT,
                                              shipping_address TEXT,
                                              billing_address TEXT,
                                              payment_method TEXT,
                                              transaction_key UUID,
                                              currency TEXT,
                                              refund_status TEXT,
                                              refunded_amount DECIMAL,
                                              PRIMARY KEY (user_id, created_at)
) WITH CLUSTERING ORDER BY (created_at DESC);
