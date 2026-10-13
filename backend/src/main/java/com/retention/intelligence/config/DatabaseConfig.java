package com.retention.intelligence.config;

import com.retention.intelligence.multitenancy.TenantRoutingDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${SPRING_DATASOURCE_URL:${spring.datasource.url:}}")
    private String rawUrl;

    @Value("${SPRING_DATASOURCE_USERNAME:${spring.datasource.username:retention_user}}")
    private String username;

    @Value("${SPRING_DATASOURCE_PASSWORD:${spring.datasource.password:retention_password}}")
    private String password;

    @Value("${spring.datasource.driver-class-name:org.postgresql.Driver}")
    private String driverClassName;

    private DataSource buildMasterDataSource() {
        HikariConfig config = new HikariConfig();

        String dbUrl = rawUrl;
        String dbUser = username;
        String dbPass = password;

        if (dbUrl != null && !dbUrl.trim().isEmpty()) {
            String cleanUrl = dbUrl.trim();
            if (cleanUrl.startsWith("jdbc:")) {
                cleanUrl = cleanUrl.substring("jdbc:".length());
            }

            if (cleanUrl.startsWith("postgres://") || cleanUrl.startsWith("postgresql://")) {
                try {
                    URI uri = new URI(cleanUrl);
                    if (uri.getUserInfo() != null) {
                        String[] userInfo = uri.getUserInfo().split(":");
                        dbUser = userInfo[0];
                        if (userInfo.length > 1) {
                            dbPass = userInfo[1];
                        }
                    }
                    int port = uri.getPort() == -1 ? 5432 : uri.getPort();
                    String path = uri.getPath();
                    dbUrl = "jdbc:postgresql://" + uri.getHost() + ":" + port + path;
                } catch (Exception e) {
                    log.warn("Failed to parse database URI, falling back to prepending jdbc: {}", e.getMessage());
                    if (!dbUrl.startsWith("jdbc:")) {
                        dbUrl = "jdbc:" + dbUrl;
                    }
                }
            } else if (!dbUrl.startsWith("jdbc:")) {
                dbUrl = "jdbc:" + dbUrl;
            }
        } else {
            dbUrl = "jdbc:postgresql://localhost:5432/retention_db";
        }

        log.info("Master DataSource JDBC URL: {}", dbUrl);
        config.setJdbcUrl(dbUrl);
        if (dbUser != null && !dbUser.trim().isEmpty()) {
            config.setUsername(dbUser);
        }
        if (dbPass != null && !dbPass.trim().isEmpty()) {
            config.setPassword(dbPass);
        }
        if (driverClassName != null && !driverClassName.trim().isEmpty()) {
            config.setDriverClassName(driverClassName);
        }
        config.setPoolName("HikariPool-Master");
        config.setMaximumPoolSize(15);
        config.setMinimumIdle(3);

        return new HikariDataSource(config);
    }

    @Bean
    @Primary
    public TenantRoutingDataSource dataSource() {
        DataSource masterDataSource = buildMasterDataSource();
        log.info("Configuring TenantRoutingDataSource with Master fallback");
        return new TenantRoutingDataSource(masterDataSource);
    }
}
