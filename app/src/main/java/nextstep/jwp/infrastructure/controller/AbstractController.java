package nextstep.jwp.infrastructure.controller;

import nextstep.jwp.model.web.ContentType;
import nextstep.jwp.model.web.HttpMethod;
import nextstep.jwp.model.web.request.CustomHttpRequest;
import nextstep.jwp.model.web.response.CustomHttpResponse;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractController implements HttpRequestHandler {

    @Override
    public void service(CustomHttpRequest request, CustomHttpResponse response) throws Exception {
        if (request.getMethod() == HttpMethod.GET) {
            doGet(request, response);
        }

        if (request.getMethod() == HttpMethod.POST) {
            doGet(request, response);
        }
    }

    protected void doGet(CustomHttpRequest request, CustomHttpResponse response) throws Exception {
    }

    protected void doPost(CustomHttpRequest request, CustomHttpResponse response) throws Exception {
    }

    protected Map<String, String> headers(int resourceLength) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", ContentType.HTML.getContentType());
        headers.put("Content-Length", resourceLength + "");

        return headers;
    }
}
