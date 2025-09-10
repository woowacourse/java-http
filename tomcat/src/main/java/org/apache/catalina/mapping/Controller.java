package org.apache.catalina.mapping;

import org.apache.coyote.util.request.HttpRequest;
import org.apache.coyote.util.response.HttpResponse;

public interface Controller {
    void service(HttpRequest request, HttpResponse response) throws Exception;
}
