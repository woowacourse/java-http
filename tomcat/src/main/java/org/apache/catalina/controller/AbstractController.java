package org.apache.catalina.controller;

import org.apache.catalina.PathFilter;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import static org.apache.coyote.http11.Method.GET;
import static org.apache.coyote.http11.Method.POST;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.isMethod(GET)) {
            PathFilter.doFilter(httpRequest);
            doGet(httpRequest, httpResponse);
        } else if (httpRequest.isMethod(POST)) {
            doPost(httpRequest, httpResponse);
        }
    }

    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {

    }
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {

    }
}
