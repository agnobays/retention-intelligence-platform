package com.retention.intelligence.multitenancy;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class TenantFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(TenantFilter.class);

    public static final String TENANT_HEADER = "X-Tenant-ID";
    public static final String TENANT_PARAM = "tenantId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest httpRequest) {
            String tenantId = httpRequest.getHeader(TENANT_HEADER);

            if (tenantId == null || tenantId.isBlank()) {
                tenantId = httpRequest.getParameter(TENANT_PARAM);
            }

            // Also check for subdomain or company slug if header not present
            if (tenantId != null && !tenantId.isBlank()) {
                TenantContext.setTenantId(tenantId.trim());
                log.debug("HTTP Request [{}] set active TenantContext: {}", httpRequest.getRequestURI(), tenantId);
            } else {
                TenantContext.setTenantId(TenantContext.DEFAULT_TENANT);
            }

            if (response instanceof HttpServletResponse httpResponse) {
                httpResponse.setHeader(TENANT_HEADER, TenantContext.getTenantId());
            }
        }

        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
