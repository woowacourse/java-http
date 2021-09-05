package nextstep.jwp.controller;

import java.util.Arrays;
import java.util.List;
import nextstep.jwp.constants.HttpMethod;
import nextstep.jwp.exception.HttpException;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.request.RequestLine;

public class MappingHandler {
    private final HttpRequest httpRequest;
    private final RequestLine requestLine;
    private final Controller controller;
    private final List<Handler> handlers;

    public MappingHandler(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
        this.requestLine = httpRequest.getRequestLine();
        this.controller = new Controller();
        this.handlers = Arrays.asList(new GetHandler(), new PostHandler());
    }

    public String response() throws Exception {
        final HttpMethod httpMethod = requestLine.getHttpMethod();
        final Handler handler = findHandler(httpMethod);
        return handler.handle(httpRequest, controller);
    }

    private Handler findHandler(HttpMethod httpMethod) {
        return handlers.stream()
                .filter(handler -> handler.matchHttpMethod(httpMethod))
                .findFirst()
                .orElseThrow(() -> new HttpException("설정되어 있지 않은 http 메소드입니다."));
    }
}
