package com.retention.intelligence.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${SPRING_DATASOURCE_URL:${spring.datasource.url:}}")
    private String rawUrl;

    @Value("${SPRING_DATASOURCE_USERNAME:${spring.datasource.username:}}")
    private String username;

    @Value("${SPRING_DATASOURCE_PASSWORD:${spring.datasource.password:}}")
    private String password;

    @Value("${spring.datasource.driver-class-name:org.postgresql.Driver}")
    private String driverClassName;

    @Bean
    @Primary
    @Profile("prod")
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();

        String dbUrl = rawUrl;
        String dbUser = username;
        String dbPass = password;

        log.info("Initializing production DataSource with raw URL length: {}", dbUrl != null ? dbUrl.length() : 0);

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
        }

        log.info("Final formatted JDBC URL: {}", dbUrl);
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

        return new HikariDataSource(config);
    }
}
