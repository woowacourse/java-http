package nextstep.jwp.controller;

import nextstep.jwp.HttpRequest;
import nextstep.jwp.HttpResponse;

public interface Controller {
    void get(HttpRequest request, HttpResponse response);
    void post(HttpRequest request, HttpResponse response);
    void put(HttpRequest request, HttpResponse response);
    void patch(HttpRequest request, HttpResponse response);
    void delete(HttpRequest request, HttpResponse response);
}
