package org.apache.catalina.controller;

import org.apache.catalina.exception.ApplicationException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws ApplicationException {
        if (request.isGET()) {
            doGet(request, response);
        } else if (request.isPOST()) {
            doPost(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws ApplicationException {
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws ApplicationException {
    }
}
