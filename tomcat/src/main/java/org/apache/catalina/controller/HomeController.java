package org.apache.catalina.controller;

import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpResponseHeader;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.StaticResourceExtension;
import org.apache.coyote.http11.message.response.StatusLine;

public class HomeController implements Controller {

    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        StatusLine statusLine = new StatusLine(HttpStatus.OK, request);

        HttpResponseHeader httpResponseHeader = new HttpResponseHeader();
        httpResponseHeader.add("Content-Type",
                StaticResourceExtension.findMimeTypeByUrl(request.getPath()) + ";charset=utf-8");
        httpResponseHeader.add("Content-Length",
                String.valueOf(DEFAULT_RESPONSE_BODY.getBytes(StandardCharsets.UTF_8).length));

        response.setStatusLine(statusLine);
        response.setHttpResponseHeader(httpResponseHeader);
        response.setResponseBody(DEFAULT_RESPONSE_BODY);
    }
}
