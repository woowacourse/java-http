package nextstep.jwp.dispatcher.mapping;

import java.util.Objects;
import nextstep.jwp.context.ApplicationContext;
import nextstep.jwp.dispatcher.handler.Handler;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.http.HttpRequest;

public class HttpRequestHandlerMapping implements HandlerMapping {

    @Override
    public boolean supports(HttpRequest httpRequest) {
        return !isFileRequest(httpRequest.getRequestURI());
    }

    private boolean isFileRequest(String urlPath) {
        return urlPath.indexOf(".") != -1;
    }

    @Override
    public Handler getHandler(HttpRequest httpRequest) {
        String urlPath = httpRequest.getRequestURI();
        ApplicationContext applicationContext = httpRequest.getApplicationContext();
        Handler mappedHandler = applicationContext.getHandler(urlPath);
        if (Objects.isNull(mappedHandler)) {
            throw new NotFoundException();
        }
        return mappedHandler;
    }
}
