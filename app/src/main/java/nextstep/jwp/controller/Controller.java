package nextstep.jwp.controller;

import nextstep.jwp.http.HttpRequest;

public interface Controller {

    boolean isMatchingController(HttpRequest httpRequest);

    String doService(HttpRequest httpRequest);

}
