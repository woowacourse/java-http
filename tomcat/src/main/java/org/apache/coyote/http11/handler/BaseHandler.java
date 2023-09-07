package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBuilder;

import static org.apache.coyote.http11.common.HttpStatus.OK;

public class BaseHandler {

    private BaseHandler() {
    }

    public static HttpResponse handle() {
        final String responseBody = "Hello world!";

        return new HttpResponseBuilder().init()
                .httpStatus(OK)
                .header("Content-Type: text/html;charset=utf-8 ")
                .header("Content-Length: " + responseBody.getBytes().length + " ")
                .body(responseBody)
                .build();
    }
}
