package nextstep.jwp.web.handler;

import nextstep.jwp.web.http.request.HttpRequest;
import nextstep.jwp.web.http.response.HttpResponse;

public interface WebHandler {

    void doHandle(HttpRequest request, HttpResponse response);
}
