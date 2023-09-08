package org.apache.catalina.servlet.filter;

import org.apache.coyote.http11.request.HttpRequest;

public interface Interceptor {

    boolean preHandle(HttpRequest request);
}