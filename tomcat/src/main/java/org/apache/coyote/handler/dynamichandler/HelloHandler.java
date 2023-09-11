package org.apache.coyote.handler.dynamichandler;

import org.apache.coyote.handler.statichandler.ExceptionHandler;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class HelloHandler extends AbstractHandler {

    @Override
    void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        String body = "Hello world!";

        httpResponse.setStatusLine(httpRequest.httpVersion(), HttpStatus.OK);
        httpResponse.setHeader(HttpHeader.CONTENT_TYPE.value(), ContentType.TEXT_HTML.value());
        httpResponse.setHeader(HttpHeader.CONTENT_LENGTH.value(), String.valueOf(body.getBytes().length));
        httpResponse.setBody(body);
    }

    @Override
    void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        new ExceptionHandler(HttpStatus.INTERNAL_SERVER_ERROR).service(httpRequest, httpResponse);
    }
}
