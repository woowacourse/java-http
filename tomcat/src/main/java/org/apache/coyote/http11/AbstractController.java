package org.apache.coyote.http11;

import java.io.IOException;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        if ("GET".equals(request.getMethod())) {
            doGet(request, response);
        }

        if ("POST".equals(request.getMethod())) {
            doPost(request, response);
        }
    }

    protected abstract void doGet(HttpRequest request, HttpResponse response) throws IOException;

    protected abstract void doPost(HttpRequest request, HttpResponse response) throws IOException;
}
