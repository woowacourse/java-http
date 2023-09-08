package org.apache.coyote.http11;

import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Mapper {
    void addController(String path, Controller controller);

    void service(final HttpRequest httpRequest, final HttpResponse httpResponse);
}
