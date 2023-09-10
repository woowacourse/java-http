package org.apache.catalina.core;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public interface Servlet {

    void service(final HttpRequest request, final HttpResponse response) throws Exception;
}
