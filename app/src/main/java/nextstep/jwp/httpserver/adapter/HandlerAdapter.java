package nextstep.jwp.httpserver.adapter;

import java.io.IOException;
import java.net.URISyntaxException;

import nextstep.jwp.httpserver.domain.View;
import nextstep.jwp.httpserver.domain.request.HttpRequest;

public interface HandlerAdapter {
    boolean supports(Object handler);

    View handle(HttpRequest httpRequest, Object handler) throws URISyntaxException, IOException;
}
