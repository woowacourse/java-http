package nextstep.jwp.handler;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestLine;

public interface Handler {
    boolean mapping(HttpRequest httpRequest);

    ResponseEntity service(HttpRequest httpRequest);
}
