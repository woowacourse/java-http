package nextstep.jwp.model.handler;

import nextstep.jwp.exception.NotFoundHandlerException;

import java.util.Arrays;
import java.util.function.Predicate;

public enum HandlerMapper {
    RESOURCE(uri -> uri.contains("."), new ResourceHandler()),
    LOGIN(uri -> uri.equals("/login"), new LoginHandler())
    ;

    private final Predicate<String> predicate;
    private final CustomHandler customHandler;

    HandlerMapper(Predicate<String> predicate, CustomHandler customHandler) {
        this.predicate = predicate;
        this.customHandler = customHandler;
    }

    public static CustomHandler from(String uri) {
        return Arrays.stream(values())
                .filter(handlerMapper -> handlerMapper.isTrue(uri))
                .findAny()
                .orElseThrow(() -> new NotFoundHandlerException())
                .getCustomHandler()
                ;
    }

    public CustomHandler getCustomHandler() {
        return customHandler;
    }

    public boolean isTrue(String uri) {
        return this.predicate.test(uri);
    }
}
