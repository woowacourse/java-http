package nextstep.jwp.controller;

import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.response.HttpResponse;

public interface Controller {
    void process(HttpRequest request, HttpResponse response) throws Exception;
}
