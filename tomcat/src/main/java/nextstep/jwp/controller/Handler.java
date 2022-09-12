package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public interface Handler {
    void handle(final HttpRequest httpRequest, final HttpResponse httpResponse);
}
