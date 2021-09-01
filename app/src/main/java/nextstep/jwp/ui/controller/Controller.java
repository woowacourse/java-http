package nextstep.jwp.ui.controller;

import nextstep.jwp.ui.request.HttpRequest;
import nextstep.jwp.ui.response.HttpResponse;

public interface Controller {
    void service(HttpRequest request, HttpResponse response);
}
