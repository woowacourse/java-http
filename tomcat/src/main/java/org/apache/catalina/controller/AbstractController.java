package org.apache.catalina.controller;

import java.io.IOException;
import org.apache.catalina.controller.http.request.HttpMethod;
import org.apache.catalina.controller.http.request.HttpRequest;
import org.apache.catalina.controller.http.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        if (request.getMethod() == HttpMethod.GET) {
            doGet(request, response);
            return;
        }
        if (request.getMethod() == HttpMethod.POST) {
            doPost(request, response);
        }
    }

    abstract void doGet(HttpRequest request, HttpResponse response) throws IOException;

    abstract void doPost(HttpRequest request, HttpResponse response) throws IOException;
}
