package org.apache.coyote.http11;

import jakarta.servlet.http.HttpServletResponse;

public interface HttpRequestHandler {

    boolean supports(HttpRequest request);

    HttpServletResponse handle(HttpRequest request);
}
