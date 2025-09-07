package org.apache.coyote;

import org.apache.catalina.request.ServletRequest;
import org.apache.catalina.response.ServletResponse;

public interface HttpRequestHandler {

    void handleGet(ServletRequest request, ServletResponse response);

    void handlePost(ServletRequest request, ServletResponse response);
}
