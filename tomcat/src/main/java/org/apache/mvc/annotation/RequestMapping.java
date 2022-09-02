package org.apache.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.coyote.http11.request.RequestMethod;
import org.apache.coyote.http11.response.headers.ContentType;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
public @interface RequestMapping {

    String value() default "/";

    RequestMethod method() default RequestMethod.GET;
}
