package com.techcourse.resolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.coyote.http11.request.HttpMethod;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Method {
    HttpMethod value() default HttpMethod.GET;
}
