package org.apache.coyote.dynamichandler;

import org.apache.coyote.handler.Handler;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.statichandler.ExceptionHandler;

public abstract class AbstractHandler implements Handler {

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.getMethod() == HttpMethod.GET) {
            doGet(httpRequest, httpResponse);
            return;
        }

        if (httpRequest.getMethod() == HttpMethod.POST) {
            doPost(httpRequest, httpResponse);
            return;
        }

        new ExceptionHandler(HttpStatus.INTERNAL_SERVER_ERROR)
                .service(httpRequest, httpResponse);
    }

    public abstract void doGet(HttpRequest httpRequest, HttpResponse httpResponse);

    public abstract void doPost(HttpRequest httpRequest, HttpResponse httpResponse);

}
