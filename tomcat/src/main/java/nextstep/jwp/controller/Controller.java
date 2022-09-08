package nextstep.jwp.controller;

import web.request.HttpRequest;
import web.response.HttpResponse;

public interface Controller {

    void service(final HttpRequest httpRequest, final HttpResponse httpResponse);
}
