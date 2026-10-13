package com.retention.intelligence.multitenancy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TenantContext {

    private static final Logger log = LoggerFactory.getLogger(TenantContext.class);
    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    public static final String DEFAULT_TENANT = "default";

    private TenantContext() {}

    public static void setTenantId(String tenantId) {
        log.debug("Setting TenantContext to: {}", tenantId);
        CURRENT_TENANT.set(tenantId != null && !tenantId.isBlank() ? tenantId : DEFAULT_TENANT);
    }

    public static String getTenantId() {
        String tenant = CURRENT_TENANT.get();
        return tenant != null ? tenant : DEFAULT_TENANT;
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
