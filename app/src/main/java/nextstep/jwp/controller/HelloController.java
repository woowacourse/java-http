package nextstep.jwp.controller;

import nextstep.jwp.http.common.MediaType;
import nextstep.jwp.http.controller.AbstractController;
import nextstep.jwp.http.message.builder.HttpResponseBuilder;
import nextstep.jwp.http.message.request.HttpRequestMessage;
import nextstep.jwp.http.message.response.HttpResponseMessage;

public class HelloController extends AbstractController {

    @Override
    protected HttpResponseMessage doGet(HttpRequestMessage httpRequestMessage) {
        String body = "Hello world!";

        return HttpResponseBuilder.ok(body)
                .contentType(MediaType.TEXT_HTML_CHARSET_UTF8)
                .contentLength(body)
                .build();
    }
}
