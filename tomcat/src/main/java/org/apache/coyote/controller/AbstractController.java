package org.apache.coyote.controller;

import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        String method = request.getMethod();
        if (method.equals("POST")) {
            doPost(request, response);
        }
        if (method.equals("GET")) {
            doGet(request, response);
        }
        // todo: 맵핑되는 메서드가 없는 경우, 에러 핸들링
    }

    abstract void doPost(HttpRequest request, HttpResponse response) throws Exception;

    abstract void doGet(HttpRequest request, HttpResponse response) throws Exception;
}
