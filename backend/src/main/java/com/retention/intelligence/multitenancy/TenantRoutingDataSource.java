package com.retention.intelligence.multitenancy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TenantRoutingDataSource extends AbstractRoutingDataSource {

    private static final Logger log = LoggerFactory.getLogger(TenantRoutingDataSource.class);

    private final Map<Object, Object> targetDataSourcesMap = new ConcurrentHashMap<>();

    public TenantRoutingDataSource(Object defaultTargetDataSource) {
        setDefaultTargetDataSource(defaultTargetDataSource);
        targetDataSourcesMap.put(TenantContext.DEFAULT_TENANT, defaultTargetDataSource);
        setTargetDataSources(targetDataSourcesMap);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        String tenant = TenantContext.getTenantId();
        log.trace("TenantRoutingDataSource resolved lookup key: {}", tenant);
        return tenant;
    }

    public synchronized void addTenantDataSource(String tenantId, Object dataSource) {
        log.info("Adding dynamic tenant DataSource for tenant ID: {}", tenantId);
        targetDataSourcesMap.put(tenantId, dataSource);
        setTargetDataSources(targetDataSourcesMap);
        afterPropertiesSet();
    }

    public boolean hasTenantDataSource(String tenantId) {
        return targetDataSourcesMap.containsKey(tenantId);
    }
}
