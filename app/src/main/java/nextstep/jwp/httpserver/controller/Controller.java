package nextstep.jwp.httpserver.controller;

import java.util.Map;

import nextstep.jwp.httpserver.domain.request.HttpRequest;
import nextstep.jwp.httpserver.domain.response.HttpResponse;

public interface Controller {
    HttpResponse service(HttpRequest httpRequest, Map<String, String> param);
}
