package nextstep.jwp.httpserver.mapping;

import nextstep.jwp.httpserver.controller.Controller;
import nextstep.jwp.httpserver.domain.request.HttpRequest;

public interface HandlerMapping {

    boolean isHandle(HttpRequest httpRequest);

    Controller find(HttpRequest httpRequest);
}
