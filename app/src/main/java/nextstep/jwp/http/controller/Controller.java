package nextstep.jwp.http.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.Response;

public interface Controller {
    Response doService(HttpRequest httpRequest);
}
