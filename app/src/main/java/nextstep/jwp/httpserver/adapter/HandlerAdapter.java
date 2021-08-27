package nextstep.jwp.httpserver.adapter;

import java.io.IOException;
import java.net.URISyntaxException;

import nextstep.jwp.httpserver.domain.View;

public interface HandlerAdapter {
    boolean supports(Object handler);

    View handle(Object handler) throws URISyntaxException, IOException;
}
