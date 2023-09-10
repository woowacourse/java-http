package org.apache.coyote.handler.dynamichandler;

import org.apache.coyote.handler.Handler;
import org.apache.coyote.handler.statichandler.ExceptionHandler;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public abstract class AbstractHandler implements Handler {

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (HttpMethod.GET == httpRequest.httpMethod()) {
            doGet(httpRequest, httpResponse);
            return;
        }
        if (HttpMethod.POST == httpRequest.httpMethod()) {
            doPost(httpRequest, httpResponse);
            return;
        }
        new ExceptionHandler(HttpStatus.INTERNAL_SERVER_ERROR).service(httpRequest, httpResponse);
    }

    abstract void doGet(HttpRequest httpRequest, HttpResponse httpResponse);

    abstract void doPost(HttpRequest httpRequest, HttpResponse httpResponse);
}
