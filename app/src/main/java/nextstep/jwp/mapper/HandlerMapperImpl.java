package nextstep.jwp.mapper;

import java.util.Arrays;
import java.util.List;
import nextstep.jwp.exception.BadRequestException;
import nextstep.jwp.handler.Handler;
import nextstep.jwp.handler.LoginController;
import nextstep.jwp.handler.RegisterController;
import nextstep.jwp.handler.ResourceHandler;
import nextstep.jwp.handler.service.LoginService;
import nextstep.jwp.handler.service.RegisterService;
import nextstep.jwp.http.request.RequestLine;

public class HandlerMapperImpl implements HandlerMapper {

    private final List<Handler> handlers = Arrays.asList(
            new LoginController(new LoginService()),
            new RegisterController(new RegisterService()),
            new ResourceHandler()
    );

    @Override
    public Handler findHandler(RequestLine requestLine) {
        return handlers.stream()
                .filter(handler -> handler.mapping(requestLine))
                .findFirst()
                .orElseThrow(BadRequestException::new);
    }
}
