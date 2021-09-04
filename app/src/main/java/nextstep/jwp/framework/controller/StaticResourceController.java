package nextstep.jwp.framework.controller;

import nextstep.jwp.framework.message.builder.HttpResponseBuilder;
import nextstep.jwp.framework.message.request.HttpRequestMessage;
import nextstep.jwp.framework.message.response.HttpResponseMessage;

public class StaticResourceController extends AbstractController {

    @Override
    protected HttpResponseMessage doGet(HttpRequestMessage httpRequestMessage) {
        String path = httpRequestMessage.requestUri();
        return HttpResponseBuilder.staticResource(path)
                .build();
    }
}
