package nextstep.jwp.dispatcher.adapter;

import nextstep.jwp.dispatcher.handler.Handler;
import nextstep.jwp.dispatcher.mapping.HandlerMapping;
import nextstep.jwp.dispatcher.mapping.HttpRequestHandlerMapping;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

public class HttpRequestHandlerAdapter implements HandlerAdapter {

    @Override
    public boolean supports(HandlerMapping handlerMapping) {
        return handlerMapping instanceof HttpRequestHandlerMapping;
    }

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse, Handler handler) {
        handler.service(httpRequest, httpResponse);
    }
}
