package org.apache.coyote.controller;

import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.httpmessage.common.ContentType;
import org.apache.coyote.http11.httpmessage.request.Request;
import org.apache.coyote.http11.httpmessage.response.Response;

public class HelloWorldController extends AbstractController {

    @Override
    public void doGet(Request request, Response response) throws Exception {
        final String responseBody = "Hello world!";

        response.ok(responseBody)
                .addHeader("Content-Type", ContentType.HTML.getContentType() + ";charset=utf-8")
                .addHeader("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
    }
}
