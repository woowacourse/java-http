package org.apache.catalina.controller;

import java.io.IOException;
import org.apache.catalina.util.ResourceReader;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class IndexController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        HttpMethod method = request.getMethod();

        if (method.isGet()) {
            doGet(response);
        }
    }

    public void doGet(HttpResponse response) throws IOException {
        ResourceReader.serveResource("/index.html", response);
    }
}
