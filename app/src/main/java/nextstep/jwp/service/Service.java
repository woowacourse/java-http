package nextstep.jwp.service;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public interface Service {

    void doService(HttpRequest request, HttpResponse response);
}
