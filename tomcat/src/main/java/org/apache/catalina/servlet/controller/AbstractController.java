package org.apache.catalina.servlet.controller;

import org.apache.catalina.connector.HttpRequest;
import org.apache.catalina.connector.HttpResponse;
import org.apache.catalina.servlet.Controller;
import org.apache.tomcat.http.common.Method;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.isSameMethod(Method.GET)) {
            doGet(request, response);
        }
        if (request.isSameMethod(Method.POST)) {
            doPost(request, response);
        }
    }

    protected abstract void doPost(HttpRequest request, HttpResponse response) throws Exception;

    protected abstract void doGet(HttpRequest request, HttpResponse response) throws Exception;
}
