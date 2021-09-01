package nextstep.jwp.controller;

import java.io.IOException;

import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.response.HttpResponse;

public interface Controller {
    void process(HttpRequest request, HttpResponse response) throws IOException;
}
