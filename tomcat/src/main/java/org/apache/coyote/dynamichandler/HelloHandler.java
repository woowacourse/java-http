package org.apache.coyote.dynamichandler;

import static org.apache.coyote.http11.HttpHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.HttpHeader.CONTENT_TYPE;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.statichandler.ExceptionHandler;

public class HelloHandler extends AbstractHandler {

    @Override
    public void doGet(
            HttpRequest httpRequest,
            HttpResponse httpResponse
    ) {
        String body = "Hello world!";

        httpResponse.setStatus(HttpStatus.OK);
        httpResponse.setHeader(CONTENT_TYPE.getValue(), ContentType.HTML.getValue());
        httpResponse.setHeader(CONTENT_LENGTH.getValue(), String.valueOf(body.getBytes().length));
        httpResponse.setResponseBody(body);
    }

    @Override
    public void doPost(
            HttpRequest httpRequest,
            HttpResponse httpResponse
    ) {
        new ExceptionHandler(HttpStatus.NOT_FOUND).service(httpRequest, httpResponse);
    }

}
