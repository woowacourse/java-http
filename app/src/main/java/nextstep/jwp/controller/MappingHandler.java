package nextstep.jwp.controller;

import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.constants.HttpMethod;
import nextstep.jwp.exception.HttpException;
import nextstep.jwp.request.RequestBody;
import nextstep.jwp.request.RequestLine;

public class MappingHandler {
    private final RequestLine requestLine;
    private final RequestBody requestBody;
    private final Controller controller;

    public MappingHandler(RequestLine requestLine, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestBody = requestBody;
        this.controller = new Controller();
    }

    public String response() throws Exception {
        final HttpMethod httpMethod = requestLine.getHttpMethod();
        final String uri = requestLine.getUri();
        final Handler handler = findHandler(httpMethod);
        return handler.runController(uri, controller);
    }

    private Handler findHandler(HttpMethod httpMethod) {
        final List<Handler> handlers = new ArrayList<>();
        handlers.add(new GetHandler());
        handlers.add(new PostHandler(requestBody));

        return handlers.stream()
                .filter(handler -> handler.matchHttpMethod(httpMethod))
                .findFirst()
                .orElseThrow(() -> new HttpException("설정되어 있지 않은 http 메소드입니다."));
    }
}
