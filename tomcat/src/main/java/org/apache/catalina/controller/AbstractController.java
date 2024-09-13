package org.apache.catalina.controller;

import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        HttpMethod httpMethod = request.getHttpMethod();
        if (httpMethod.isPost()) {
            doPost(request, response);
        }
        if (httpMethod.isGet()) {
            doGet(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected final byte[] readStaticResource(String path) {
        return StaticResourceReader.readResource(getClass().getClassLoader(), path);
    }
}
