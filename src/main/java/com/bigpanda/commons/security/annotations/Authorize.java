package com.bigpanda.commons.security.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Authorize {
    String[] value();
    AuthorizeEnforcementType enforcementType() default AuthorizeEnforcementType.ANY;
}