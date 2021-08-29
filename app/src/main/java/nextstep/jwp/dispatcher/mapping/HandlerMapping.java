package nextstep.jwp.dispatcher.mapping;

import nextstep.jwp.dispatcher.handler.Handler;
import nextstep.jwp.http.HttpRequest;

public interface HandlerMapping {

    boolean supports(HttpRequest httpRequest);

    Handler getHandler(HttpRequest httpRequest);
}
