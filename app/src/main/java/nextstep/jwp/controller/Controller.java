package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public interface Controller {
    HttpResponse doService(final HttpRequest httpRequest);

    boolean canHandle(final HttpRequest httpRequest);
}
