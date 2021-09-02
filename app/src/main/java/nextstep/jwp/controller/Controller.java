package nextstep.jwp.controller;

import nextstep.jwp.framework.http.request.HttpRequest;
import nextstep.jwp.framework.http.response.HttpResponse;

public interface Controller {

    void service(HttpRequest request, HttpResponse response) throws Exception;
}
