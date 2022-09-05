package nextstep.jwp.controller;

import org.apache.coyote.web.request.HttpRequest;
import org.apache.coyote.web.response.SimpleHttpResponse;

public interface Controller {

    void service(HttpRequest httpRequest, SimpleHttpResponse httpResponse);
}
