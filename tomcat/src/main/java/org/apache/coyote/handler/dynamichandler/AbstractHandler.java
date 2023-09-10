package org.apache.coyote.handler.dynamichandler;

import org.apache.coyote.handler.Handler;
import org.apache.coyote.handler.statichandler.ExceptionHandler;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public abstract class AbstractHandler implements Handler {

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        if(HttpMethod.GET == httpRequest.httpMethod()) {
            return doGet(httpRequest);
        }
        if(HttpMethod.POST == httpRequest.httpMethod()) {
            return doPost(httpRequest);
        }
        return new ExceptionHandler(HttpStatus.INTERNAL_SERVER_ERROR).handle(httpRequest);
    }

    abstract HttpResponse doGet(HttpRequest httpRequest);

    abstract HttpResponse doPost(HttpRequest httpRequest);
}
