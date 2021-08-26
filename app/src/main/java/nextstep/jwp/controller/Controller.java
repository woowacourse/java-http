package nextstep.jwp.controller;

import nextstep.jwp.HttpRequest;
import nextstep.jwp.HttpResponse;

public interface Controller {
    HttpResponse get(HttpRequest request);
}
