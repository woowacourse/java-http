package org.apache.coyote.http11.request.mapping.controllerscan;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.coyote.http11.HttpMethod;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {

    HttpMethod method() default HttpMethod.GET;

    String uri() default "/";
}
