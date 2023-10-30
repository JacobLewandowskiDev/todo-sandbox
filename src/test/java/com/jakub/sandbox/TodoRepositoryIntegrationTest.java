package com.jakub.sandbox;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.BufferedInputStream;

public class TodoRepositoryIntegrationTest {
//    private static final String POSTGRES_VERSION = "POSTGRES:15.3";
//    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(POSTGRES_VERSION);
//    private static JdbcTemplate JDBC_TEMPLATE;
//
//    @BeforeAll
//    static void allSetup() throws Exception {
//        POSTGRES.start();
//        JDBC_TEMPLATE = jdbcTemplate();
//        initSchema();
//    }
//
//    private static void initSchema() throws Exception {
//        try (var is = new BufferedInputStream(TodoRepositoryIntegrationTest.class.getResourceAsStream("/schema.sql"))) {
//           var content = is.readAllBytes();
//           var sql = new String(content);
//        }
//    }
}
