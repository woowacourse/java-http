package nextstep.jwp.model.handler;

import nextstep.jwp.exception.NotFoundHandlerException;

import java.util.Arrays;
import java.util.function.Predicate;

public enum HandlerMapper {
    RESOURCE(uri -> uri.contains("."), new ResourceHandler()),
    LOGIN(uri -> uri.equals("/login"), new LoginHandler()),
    REGISTER(uri -> uri.equals("/register"), new RegisterHandler())
    ;

    private final Predicate<String> predicate;
    private final CustomHandler customHandler;

    HandlerMapper(Predicate<String> predicate, CustomHandler customHandler) {
        this.predicate = predicate;
        this.customHandler = customHandler;
    }

    public static CustomHandler from(String uri) {
        return Arrays.stream(values())
                .filter(handlerMapper -> handlerMapper.isSatisfied(uri))
                .findAny()
                .orElseThrow(NotFoundHandlerException::new)
                .customHandler
                ;
    }

    private boolean isSatisfied(String uri) {
        return this.predicate.test(uri);
    }
}
