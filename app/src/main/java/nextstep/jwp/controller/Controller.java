package nextstep.jwp.controller;

import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

public interface Controller {

    void get(HttpRequest request, HttpResponse response);

    void post(HttpRequest request, HttpResponse response);

    boolean isSatisfiedBy(String httpUriPath);
}
