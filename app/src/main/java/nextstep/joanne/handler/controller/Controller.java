package nextstep.joanne.handler.controller;

import nextstep.joanne.http.request.HttpRequest2;
import nextstep.joanne.http.response.HttpResponse2;

public interface Controller {
    void service(HttpRequest2 request, HttpResponse2 response);
}
