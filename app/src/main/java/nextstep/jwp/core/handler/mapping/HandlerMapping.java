package nextstep.jwp.core.handler.mapping;

import nextstep.jwp.core.handler.Handler;
import nextstep.jwp.webserver.request.HttpRequest;

public interface HandlerMapping {
    Handler findHandler(HttpRequest httpRequest);
}
