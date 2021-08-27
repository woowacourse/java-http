package nextstep.jwp.httpserver.mapping;

import nextstep.jwp.httpserver.controller.Handler;
import nextstep.jwp.httpserver.domain.request.HttpRequest;

public interface HandlerMapping {

    boolean isHandle(HttpRequest httpRequest);

    Handler find(HttpRequest httpRequest);
}
