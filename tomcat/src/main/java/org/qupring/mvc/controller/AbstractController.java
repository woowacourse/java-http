package org.qupring.mvc.controller;

import org.apache.http.HttpMethod;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponse;
import org.qupring.file.HtmlReader;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.getHttpMethod() == HttpMethod.GET) {
            doGet(request, response);
            return;
        }

        if (request.getHttpMethod() == HttpMethod.POST) {
            doPost(request, response);
            return;
        }

        response.setBody(HtmlReader.read("static/404.html"));
        response.setStatus(404);
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setStatus(304);
        response.setLocation("/404.html");
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        response.setStatus(304);
        response.setLocation("/404.html");
    }
}
