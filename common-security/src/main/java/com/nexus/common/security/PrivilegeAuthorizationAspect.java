package com.nexus.common.security;

import com.nexus.common.core.exception.ForbiddenException;
import com.nexus.common.core.exception.UnauthorizedException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class PrivilegeAuthorizationAspect {

    @Around("@annotation(com.nexus.common.security.RequiresPrivilege)")
    public Object checkPrivilege(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String required = method.getAnnotation(RequiresPrivilege.class).value();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("UNAUTHENTICATED", "Authentication required");
        }

        boolean hasPrivilege = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(required));
        if (!hasPrivilege) {
            throw new ForbiddenException("PRIVILEGE_DENIED", "Missing required privilege: " + required);
        }

        return joinPoint.proceed();
    }
}
