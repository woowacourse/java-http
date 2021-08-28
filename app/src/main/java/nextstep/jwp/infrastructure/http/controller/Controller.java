package nextstep.jwp.infrastructure.http.controller;

import nextstep.jwp.infrastructure.http.View;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.request.HttpRequestLine;

public interface Controller {

    HttpRequestLine requestLine();
    View handle(final HttpRequest request);
}
