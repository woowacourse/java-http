package nextstep.jwp.controller;

import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

public interface Controller {
    HttpResponse service(final HttpRequest httpRequest);
}
