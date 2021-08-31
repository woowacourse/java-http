package nextstep.jwp.controller;

import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.response.HttpResponse;

public interface Controller {

    String uri();

    void service(HttpRequest request, HttpResponse response) throws Exception;
}
