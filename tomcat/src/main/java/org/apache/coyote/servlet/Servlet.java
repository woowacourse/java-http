package org.apache.coyote.servlet;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public interface Servlet {

    HttpResponse service(HttpRequest request);
}
