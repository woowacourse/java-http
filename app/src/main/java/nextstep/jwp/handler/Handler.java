package nextstep.jwp.handler;

import nextstep.jwp.http.request.HttpRequest;

public interface Handler {
    boolean mapping(HttpRequest httpRequest);

    ResponseEntity service(HttpRequest httpRequest);
}
