package com.chuwa.orderservice.config;


import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.CqlSessionFactoryBean;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

//
//
@Configuration
@EnableCassandraRepositories(basePackages = "com.chuwa.orderservice.dao")
public class CassandraConfig extends AbstractCassandraConfiguration {

    private static final Logger log = LoggerFactory.getLogger(CassandraConfig.class);
    private static final CountDownLatch latch = new CountDownLatch(1);
    private static CqlSession session;

    /**
     * Blocks Spring Boot startup until Cassandra is available.
     */
    private static void waitForCassandra() {
        CqlSessionBuilder builder = CqlSession.builder()
                .addContactPoint(new InetSocketAddress("cassandra", 9042))
                .withLocalDatacenter("datacenter1");

        AtomicInteger attempts = new AtomicInteger();
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        executor.scheduleAtFixedRate(() -> {
            try {
                session = builder.build();
                ResultSet rs = session.execute(
                        "SELECT keyspace_name FROM system_schema.keyspaces WHERE keyspace_name = 'emart_order';"
                );

                if (rs.one() != null) {
                    log.info("Cassandra keyspace 'emart_order' is available.");
                    executor.shutdown();
                    latch.countDown();  // Release the block
                } else {
                    handleFailure(attempts, "Keyspace 'emart_order' does not exist");
                }
            } catch (Exception e) {
                handleFailure(attempts, "Cassandra is unavailable");
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    private static void handleFailure(AtomicInteger attempts, String errorMessage) {
        if (attempts.incrementAndGet() >= 12) { // 12 attempts * 10s = 2 minutes
            log.error(errorMessage + " - exiting after 2 minutes");
            System.exit(1);
        }
        log.debug(errorMessage + " - retrying in 10 seconds");
    }

    @Override
    public String getKeyspaceName() {
        return "emart_order";
    }

    @Override
    protected String getContactPoints() {
        return "cassandra";  // Use Docker service name
    }

    @Override
    protected String getLocalDataCenter() {
        return "datacenter1";
    }

    @Override
    protected CqlSession getRequiredSession() {
        try {
            waitForCassandra();
            latch.await();  // Block until Cassandra is up
            log.info("Cassandra is up. Proceeding with application startup.");
            return session;  // ✅ This ensures only one session bean is created
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for Cassandra to start", e);
        }
    }
}
