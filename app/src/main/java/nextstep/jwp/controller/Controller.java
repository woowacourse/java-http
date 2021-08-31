package nextstep.jwp.controller;

import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.request.HttpRequestLine;
import nextstep.jwp.infrastructure.http.view.View;

public interface Controller {

    HttpRequestLine requestLine();

    View handle(final HttpRequest request);
}
