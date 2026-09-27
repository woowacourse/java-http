package org.apache.catalina;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Container {

    void service(HttpRequest request, HttpResponse response) throws Exception;
}
