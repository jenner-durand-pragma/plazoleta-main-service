package com.pragma.plazoleta.infrastructure.configuration.security.annotation;

import com.pragma.plazoleta.domain.constants.RoleConstants;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('" + RoleConstants.ROLE_ADMIN + "')")
public @interface IsAdmin {
}
