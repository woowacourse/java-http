package nextstep.joanne.server.handler.controller;

import nextstep.joanne.server.http.request.HttpRequest;
import nextstep.joanne.server.http.response.HttpResponse;

public interface Controller {
    void service(HttpRequest request, HttpResponse response);
}
