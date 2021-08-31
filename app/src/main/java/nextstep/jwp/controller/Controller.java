package nextstep.jwp.controller;

import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.request.HttpRequest;

public interface Controller {

    boolean isMatchingController(HttpRequest httpRequest);

    HttpResponse doService(HttpRequest httpRequest);

}
