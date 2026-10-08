package com.nexus.common.security;

// Set as Authentication.getDetails() by JwtAuthenticationFilter. Authorities already carry
// privileges (that's what @RequiresPrivilege checks), but role/trustLevel have no equivalent
// Spring Security concept to piggyback on, so a caller that needs them (e.g. auction-service
// checking trustLevel before a bid) reads them from here instead of re-parsing the token.
public record TokenDetails(String role, String trustLevel) {
}
