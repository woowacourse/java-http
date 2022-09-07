package org.apache.coyote.support;

import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.HttpRequest;

public interface Controller {

    void service(HttpRequest request, HttpResponse response) throws Exception;
}
