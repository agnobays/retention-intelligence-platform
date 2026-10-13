package com.retention.intelligence.multitenancy;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TenantDataSourceManager {

    private static final Logger log = LoggerFactory.getLogger(TenantDataSourceManager.class);

    private final TenantRoutingDataSource routingDataSource;
    private final Map<String, HikariDataSource> tenantPools = new ConcurrentHashMap<>();

    @Value("${SPRING_DATASOURCE_URL:${spring.datasource.url:}}")
    private String defaultDbUrl;

    @Value("${SPRING_DATASOURCE_USERNAME:${spring.datasource.username:retention_user}}")
    private String defaultUsername;

    @Value("${SPRING_DATASOURCE_PASSWORD:${spring.datasource.password:retention_password}}")
    private String defaultPassword;

    public TenantDataSourceManager(TenantRoutingDataSource routingDataSource) {
        this.routingDataSource = routingDataSource;
    }

    /**
     * Provisions a new physically isolated database for a given tenant,
     * runs Flyway migrations, and adds the connection pool to routing.
     */
    public synchronized DataSource provisionTenantDatabase(String tenantId, String databaseName) {
        if (tenantPools.containsKey(tenantId)) {
            log.info("Tenant {} already has an active DataSource pool", tenantId);
            return tenantPools.get(tenantId);
        }

        log.info("🚀 [MULTI-TENANCY] Provisioning physically isolated database '{}' for tenant '{}'", databaseName, tenantId);

        // 1. Ensure the PostgreSQL physical database exists
        createPhysicalDatabaseIfNotExists(databaseName);

        // 2. Build dedicated JDBC URL for the new tenant database
        String tenantJdbcUrl = buildTenantJdbcUrl(databaseName);

        // 3. Create isolated HikariCP connection pool
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(tenantJdbcUrl);
        config.setUsername(defaultUsername);
        config.setPassword(defaultPassword);
        config.setPoolName("HikariPool-Tenant-" + tenantId);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);

        HikariDataSource tenantDataSource = new HikariDataSource(config);

        // 4. Run Flyway schema migrations on the new isolated tenant database
        log.info("📦 Running Flyway migrations on isolated database for tenant: {}", tenantId);
        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(tenantDataSource)
                    .locations("classpath:db/migration")
                    .baselineOnMigrate(true)
                    .load();
            flyway.migrate();
            log.info("✅ Flyway migrations completed successfully for tenant: {}", tenantId);
        } catch (Exception e) {
            log.warn("⚠️ Flyway migration note for tenant {}: {}", tenantId, e.getMessage());
        }

        // 5. Register with TenantRoutingDataSource
        routingDataSource.addTenantDataSource(tenantId, tenantDataSource);
        tenantPools.put(tenantId, tenantDataSource);

        log.info("🎉 [MULTI-TENANCY] Tenant '{}' successfully mapped to isolated database '{}'", tenantId, databaseName);
        return tenantDataSource;
    }

    private void createPhysicalDatabaseIfNotExists(String databaseName) {
        if (defaultDbUrl == null || !defaultDbUrl.contains("postgresql")) {
            log.debug("Skipping CREATE DATABASE: not running on PostgreSQL (URL: {})", defaultDbUrl);
            return;
        }

        // Connect to master 'postgres' administrative database to execute CREATE DATABASE
        String adminUrl = defaultDbUrl.replaceAll("/[a-zA-Z0-9_-]+(\\?.*)?$", "/postgres$1");
        if (!adminUrl.startsWith("jdbc:")) {
            adminUrl = "jdbc:" + adminUrl;
        }

        try (Connection conn = DriverManager.getConnection(adminUrl, defaultUsername, defaultPassword);
             Statement stmt = conn.createStatement()) {

            // Sanitize database name to alphanumeric + underscore
            String sanitizedDbName = databaseName.replaceAll("[^a-zA-Z0-9_]", "_").toLowerCase();

            var rs = stmt.executeQuery("SELECT 1 FROM pg_database WHERE datname = '" + sanitizedDbName + "'");
            if (!rs.next()) {
                log.info("Creating new PostgreSQL database: {}", sanitizedDbName);
                stmt.execute("CREATE DATABASE " + sanitizedDbName);
            } else {
                log.info("PostgreSQL database already exists: {}", sanitizedDbName);
            }
        } catch (Exception e) {
            log.warn("Could not auto-create physical database via SQL (may already exist or lack permissions): {}", e.getMessage());
        }
    }

    private String buildTenantJdbcUrl(String databaseName) {
        String sanitized = databaseName.replaceAll("[^a-zA-Z0-9_]", "_").toLowerCase();
        if (defaultDbUrl != null && defaultDbUrl.contains("/")) {
            int lastSlash = defaultDbUrl.lastIndexOf('/');
            int queryParamIndex = defaultDbUrl.indexOf('?', lastSlash);
            String queryParams = queryParamIndex != -1 ? defaultDbUrl.substring(queryParamIndex) : "";
            String base = defaultDbUrl.substring(0, lastSlash);
            String url = base + "/" + sanitized + queryParams;
            return url.startsWith("jdbc:") ? url : "jdbc:" + url;
        }
        return "jdbc:postgresql://localhost:5432/" + sanitized;
    }

    public boolean hasTenantPool(String tenantId) {
        return tenantPools.containsKey(tenantId) || routingDataSource.hasTenantDataSource(tenantId);
    }
}
