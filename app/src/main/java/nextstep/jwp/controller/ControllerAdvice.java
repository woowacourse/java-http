package nextstep.jwp.controller;

import nextstep.jwp.exception.HttpUriNotFoundException;
import nextstep.jwp.exception.StaticFileNotFoundException;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.HttpStatusCode;
import nextstep.jwp.http.message.response.HttpResponseMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ControllerAdvice {

    private static final ControllerAdvice instance = new ControllerAdvice();

    private static final Map<Class<? extends RuntimeException>, Consumer<HttpResponseMessage>> errorHandlerInfos;

    static {
        errorHandlerInfos = new HashMap<>();
        errorHandlerInfos.put(UnauthorizedException.class, makeRedirectConsumer("/401.html"));
        errorHandlerInfos.put(HttpUriNotFoundException.class, makeRedirectConsumer("/404.html"));
        errorHandlerInfos.put(StaticFileNotFoundException.class, makeErrorResponseConsumer(HttpStatusCode.NOT_FOUND));
    }

    private ControllerAdvice() {
    }

    public static ControllerAdvice getInstance() {
        return instance;
    }

    private static Consumer<HttpResponseMessage> makeRedirectConsumer(String redirectUri) {
        return httpResponseMessage -> {
            httpResponseMessage.setStatusCode(HttpStatusCode.FOUND);
            httpResponseMessage.putHeader("Location", redirectUri);
        };
    }

    private static Consumer<HttpResponseMessage> makeErrorResponseConsumer(HttpStatusCode httpStatusCode) {
        return httpResponseMessage -> httpResponseMessage.setStatusCode(httpStatusCode);
    }

    private static Consumer<HttpResponseMessage> makeInternalServerErrorConsumer() {
        return httpResponseMessage -> httpResponseMessage.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR);
    }

    public void run(HttpResponseMessage httpResponseMessage, RuntimeException e) {
        Class<? extends RuntimeException> classType = e.getClass();
        errorHandlerInfos.getOrDefault(classType, makeInternalServerErrorConsumer())
                .accept(httpResponseMessage);
    }
}
