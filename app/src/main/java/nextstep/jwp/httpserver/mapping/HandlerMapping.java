package nextstep.jwp.httpserver.mapping;

import nextstep.jwp.httpserver.domain.request.HttpRequest;

public interface HandlerMapping {
    boolean canUse(HttpRequest httpRequest);

    Object getHandler(HttpRequest httpRequest);
}
