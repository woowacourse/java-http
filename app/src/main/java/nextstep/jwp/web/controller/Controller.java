package nextstep.jwp.web.controller;

import nextstep.jwp.web.network.request.HttpRequest;
import nextstep.jwp.web.network.response.HttpResponse;

public interface Controller {

    String getResource();

    HttpResponse execute(HttpRequest httpRequest);
}
