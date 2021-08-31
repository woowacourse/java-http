package nextstep.jwp.mvc.mapping;

import nextstep.jwp.mvc.handler.Handler;
import nextstep.jwp.webserver.request.HttpRequest;

public interface HandlerMapping {
    Handler findHandler(HttpRequest httpRequest);
}
