package nextstep.jwp.infrastructure.controller;

import nextstep.jwp.model.web.ContentType;
import nextstep.jwp.model.web.HttpMethod;
import nextstep.jwp.model.web.request.CustomHttpRequest;
import nextstep.jwp.model.web.response.CustomHttpResponse;

import java.util.Collections;

public abstract class AbstractController implements HttpRequestHandler {

    @Override
    public void service(CustomHttpRequest request, CustomHttpResponse response) throws Exception {
        if (request.getMethod() == HttpMethod.GET) {
            doGet(request, response);
        }

        if (request.getMethod() == HttpMethod.POST) {
            doPost(request, response);
        }
    }

    protected void doGet(CustomHttpRequest request, CustomHttpResponse response) throws Exception {
    }

    protected void doPost(CustomHttpRequest request, CustomHttpResponse response) throws Exception {
    }

    protected void addContentHeader(CustomHttpResponse response, int resourceLength) {
        response.addHeaders("Content-Type", Collections.singletonList(ContentType.HTML.getContentType()));
        response.addHeaders("Content-Length", Collections.singletonList(resourceLength + ""));
    }
}
