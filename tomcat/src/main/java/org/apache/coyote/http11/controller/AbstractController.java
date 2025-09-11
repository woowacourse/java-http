package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.model.HttpRequest;
import org.apache.coyote.http11.model.HttpResponse;

public class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {

    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {}
    protected void doPut(HttpRequest request, HttpResponse response) throws Exception {}
}
