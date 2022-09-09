package nextstep.jwp.controller;

import org.apache.coyote.model.request.HttpRequest;

public interface Handler {

    String getResponse(final HttpRequest httpRequest);
}
