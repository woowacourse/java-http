package nextstep.jwp.core.mvc.mapping;

import nextstep.jwp.core.mvc.Handler;
import nextstep.jwp.webserver.request.HttpRequest;

public interface HandlerMapping {
    Handler findHandler(HttpRequest httpRequest);
}
