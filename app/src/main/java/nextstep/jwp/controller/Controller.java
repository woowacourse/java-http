package nextstep.jwp.controller;

import nextstep.jwp.HttpRequest;

public interface Controller {
    void get(HttpRequest request);
    void post(HttpRequest request);
    void put(HttpRequest request);
    void patch(HttpRequest request);
    void delete(HttpRequest request);
}
