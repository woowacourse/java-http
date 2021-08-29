package nextstep.jwp.controller;

import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.common.HttpStatusCode;
import nextstep.jwp.http.exception.HttpUriNotFoundException;
import nextstep.jwp.http.exception.StaticFileNotFoundException;
import nextstep.jwp.http.message.response.HttpResponseMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ExceptionAdvice {

    private static final ExceptionAdvice instance = new ExceptionAdvice();

    private static final Map<Class<? extends RuntimeException>, Consumer<HttpResponseMessage>> errorHandlerInfos;

    static {
        errorHandlerInfos = new HashMap<>();
        errorHandlerInfos.put(UnauthorizedException.class, makeRedirectConsumer("/401.html"));
        errorHandlerInfos.put(HttpUriNotFoundException.class, makeRedirectConsumer("/404.html"));
        errorHandlerInfos.put(StaticFileNotFoundException.class, makeErrorResponseConsumer(HttpStatusCode.NOT_FOUND));
    }

    private ExceptionAdvice() {
    }

    public static ExceptionAdvice getInstance() {
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
