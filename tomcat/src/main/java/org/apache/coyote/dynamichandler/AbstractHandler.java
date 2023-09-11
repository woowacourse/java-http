package org.apache.coyote.dynamichandler;

import org.apache.coyote.handler.Handler;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractHandler implements Handler {

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.getMethod() == HttpMethod.GET) {
            doGet(httpRequest, httpResponse);
        }

        if (httpRequest.getMethod() == HttpMethod.POST) {
            doPost(httpRequest, httpResponse);
        }
    }

    abstract void doGet(HttpRequest httpRequest, HttpResponse httpResponse);

    abstract void doPost(HttpRequest httpRequest, HttpResponse httpResponse);

}
