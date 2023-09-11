package org.apache.coyote.http11;

import org.apache.catalina.servletcontainer.Handler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Container {

    void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception;

    void addHandler(String url, Handler handler);
}
