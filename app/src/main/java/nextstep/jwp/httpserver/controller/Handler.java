package nextstep.jwp.httpserver.controller;

import nextstep.jwp.httpserver.domain.response.HttpResponse;

public interface Handler {
    HttpResponse handle();
}
