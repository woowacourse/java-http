package nextstep.jwp.dispatcher.adapter;

import nextstep.jwp.dispatcher.handler.Handler;
import nextstep.jwp.dispatcher.mapping.HandlerMapping;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

public interface HandlerAdapter {

    boolean supports(HandlerMapping handlerMapping);

    void handle(HttpRequest httpRequest, HttpResponse httpResponse, Handler handler);
}
