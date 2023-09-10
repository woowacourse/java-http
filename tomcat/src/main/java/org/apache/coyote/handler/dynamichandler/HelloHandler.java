package org.apache.coyote.handler.dynamichandler;

import org.apache.coyote.handler.statichandler.ExceptionHandler;
import org.apache.coyote.http11.Body;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Header;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.StatusLine;

import java.util.LinkedHashMap;
import java.util.Map;

public class HelloHandler extends AbstractHandler {

    @Override
    HttpResponse doGet(HttpRequest httpRequest) {
        StatusLine statusLine = new StatusLine(httpRequest.httpVersion(), HttpStatus.OK);

        Body body = new Body("Hello world!");

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(HttpHeader.CONTENT_TYPE.value(), ContentType.TEXT_HTML.value());
        headers.put(HttpHeader.CONTENT_LENGTH.value(), String.valueOf(body.message().getBytes().length));
        Header header = new Header(headers);

        return new HttpResponse(statusLine, header, body);
    }

    @Override
    HttpResponse doPost(HttpRequest httpRequest) {
        return new ExceptionHandler(HttpStatus.INTERNAL_SERVER_ERROR).handle(httpRequest);
    }
}
