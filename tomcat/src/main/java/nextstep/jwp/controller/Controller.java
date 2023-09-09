package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Controller {
    boolean canProcess(HttpRequest httpRequest);

    void service(HttpRequest httpRequest, HttpResponse httpResponse);
}
