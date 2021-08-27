package nextstep.jwp.httpserver.controller;

import java.util.Map;

import nextstep.jwp.httpserver.domain.response.HttpResponse;

public interface Handler {
    HttpResponse handle(Map<String, String> param);
}
