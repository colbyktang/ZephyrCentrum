package com.ctang.zephyrsanctum;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class DatabaseConnectionTest {
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Test
    void testConnection() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            assertTrue(connection.isValid(1));
            assertNotNull(connection.getMetaData());
            System.out.println("Successfully connected to: " + 
                connection.getMetaData().getDatabaseProductName() + " " + 
                connection.getMetaData().getDatabaseProductVersion());
        }
    }
    
    @Test
    void testQuery() {
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        assertNotNull(result);
        System.out.println("Query result: " + result);
    }
}