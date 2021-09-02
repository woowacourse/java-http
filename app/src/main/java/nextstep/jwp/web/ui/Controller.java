package nextstep.jwp.web.ui;

import nextstep.jwp.server.http.request.HttpRequest;
import nextstep.jwp.server.http.response.HttpResponse;

public interface Controller {
    void service(HttpRequest request, HttpResponse response);
}
