package nextstep.jwp.controller;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public interface Controller {
    HttpResponse service(HttpRequest request);
}
