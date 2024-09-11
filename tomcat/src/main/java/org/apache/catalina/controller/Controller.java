package org.apache.catalina.controller;

import java.io.IOException;
import org.apache.catalina.controller.http.request.HttpRequest;
import org.apache.catalina.controller.http.response.HttpResponse;

public interface Controller {
    void service(HttpRequest request, HttpResponse response) throws IOException;
}
