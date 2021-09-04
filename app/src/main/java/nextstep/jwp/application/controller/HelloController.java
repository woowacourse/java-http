package nextstep.jwp.application.controller;

import nextstep.jwp.framework.common.MediaType;
import nextstep.jwp.framework.controller.AbstractController;
import nextstep.jwp.framework.message.builder.HttpResponseBuilder;
import nextstep.jwp.framework.message.request.HttpRequestMessage;
import nextstep.jwp.framework.message.response.HttpResponseMessage;

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
