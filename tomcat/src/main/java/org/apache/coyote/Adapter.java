package org.apache.coyote;

import org.apache.catalina.servlet.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Adapter {

    void service(HttpRequest request, HttpResponse response);

    void addController(String path, Controller controller);
}
