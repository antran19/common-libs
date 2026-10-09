package com.nexus.common.security;

// Optional hook for JwtAuthenticationFilter: a service that wants logout to actually revoke a
// token (e.g. user-service) provides this bean; a service that doesn't (catalog-service,
// auction-service, commerce-service) has none wired, and the filter fails open -- same
// fail-open convention already used for missing/old-token claims elsewhere in this filter.
public interface TokenBlacklistPort {
    boolean isBlacklisted(String jti);
}
