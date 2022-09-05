package nextstep.jwp.controller;

import org.apache.coyote.web.request.HttpRequest;
import org.apache.coyote.web.response.HttpResponse;

public interface Controller {

    void service(HttpRequest httpRequest, HttpResponse httpResponse);
}
