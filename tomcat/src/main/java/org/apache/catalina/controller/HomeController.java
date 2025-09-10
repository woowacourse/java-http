package org.apache.catalina.controller;

import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.message.HttpRequest;
import org.apache.coyote.http11.message.HttpResponse;
import org.apache.coyote.http11.message.HttpResponseHeader;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.StaticResourceExtension;
import org.apache.coyote.http11.message.StatusLine;

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

        response = new HttpResponse(statusLine, httpResponseHeader,
                DEFAULT_RESPONSE_BODY);
    }
}
