package org.apache;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public interface Servlet {

    void service(HttpRequest request, HttpResponse response);
}
