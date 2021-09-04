package nextstep.jwp.http.controller;

import nextstep.jwp.http.message.builder.HttpResponseBuilder;
import nextstep.jwp.http.message.request.HttpRequestMessage;
import nextstep.jwp.http.message.response.HttpResponseMessage;

public class StaticResourceController extends AbstractController {

    @Override
    protected HttpResponseMessage doGet(HttpRequestMessage httpRequestMessage) {
        String path = httpRequestMessage.requestUri();
        return HttpResponseBuilder.staticResource(path)
                .build();
    }
}
