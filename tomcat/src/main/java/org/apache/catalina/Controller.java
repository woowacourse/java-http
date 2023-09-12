package org.apache.catalina;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;

public interface Controller {

    void init();
    void destory();
    void service(HttpRequest request, HttpResponse response) throws IOException;

}
