package com.chuwa.orderservice.config;


import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.data.cassandra.config.CassandraConfiguration;
import org.springframework.data.cassandra.config.CassandraSessionFactoryBean;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;



//@Configuration
//@EnableCassandraRepositories(basePackages = "com.chuwa.orderservice.dao")
public class CassandraConfig {

    @Bean
    public CqlSessionBuilderCustomizer sessionBuilderCustomizer(CassandraProperties properties) {
        return builder -> builder.withKeyspace(properties.getKeyspaceName());
    }



}