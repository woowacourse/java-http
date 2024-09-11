package org.apache.coyote.controller;


import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (request.getMethod().isGet()) {
            doGet(request, response);
        }
        if (request.getMethod().isPost()) {
            doPost(request, response);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
    }
}
