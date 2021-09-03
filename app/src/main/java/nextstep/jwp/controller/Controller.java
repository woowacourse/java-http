package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public interface Controller {

    boolean isMatchingController(HttpRequest httpRequest);

    HttpResponse doService(HttpRequest httpRequest);

}
