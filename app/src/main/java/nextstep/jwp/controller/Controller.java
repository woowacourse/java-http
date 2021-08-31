package nextstep.jwp.controller;

import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.request.RequestLine;
import nextstep.jwp.infrastructure.http.view.View;

public interface Controller {

    RequestLine requestLine();

    View handle(final HttpRequest request);
}
