package nextstep.jwp.web.mvc.controller;

import nextstep.jwp.web.exception.ApplicationRuntimeException;
import nextstep.jwp.web.http.request.HttpRequest;
import nextstep.jwp.web.http.response.HttpResponse;

public interface Controller {
    void service(HttpRequest request, HttpResponse response) throws ApplicationRuntimeException;
}
