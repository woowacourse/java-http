package nextstep.jwp.controller;

import nextstep.jwp.web.HttpRequest;
import nextstep.jwp.web.HttpResponse;

public interface Controller {
    void service(HttpRequest request, HttpResponse response) throws Exception;
}
