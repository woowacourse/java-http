package nextstep.jwp.controller;

import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

public interface Controller {

    HttpResponse get(HttpRequest request);

    HttpResponse post(HttpRequest request);
}
