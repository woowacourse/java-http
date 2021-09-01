package nextstep.jwp.infrastructure.controller;

import nextstep.jwp.model.web.request.CustomHttpRequest;
import nextstep.jwp.model.web.response.CustomHttpResponse;

public interface HttpRequestHandler {
    void service(CustomHttpRequest request, CustomHttpResponse response) throws Exception;
}
