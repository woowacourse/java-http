package org.apache.coyote.http11.utils;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.FileController;
import nextstep.jwp.controller.HelloController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import org.apache.coyote.http11.request.HttpRequest;

public enum RequestMappingHandler {

    STRING(RequestMappingHandler::isHelloUrl, new HelloController()),
    FILE(RequestMappingHandler::isFileUrl, new FileController()),
    LOGIN_GET(RequestMappingHandler::isLoginUrl, new LoginController()),
    REGISTER_GET(RequestMappingHandler::isRegisterUrl, new RegisterController());

    private static final Pattern FILE_REGEX = Pattern.compile(".+\\.(html|css|js|ico)");

    private final Predicate<String> condition;
    private final Controller controller;

    RequestMappingHandler(final Predicate<String> condition, final Controller controller) {
        this.condition = condition;
        this.controller = controller;
    }

    public static Controller findResponseMaker(final HttpRequest httpRequest) {
        final String requestUrl = httpRequest.getRequestUrl();
        return Arrays.stream(values())
                .filter(value -> value.condition.test(requestUrl))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 url 요청입니다."))
                .getController();
    }

    public static boolean isFileUrl(final String requestUrl) {
        return FILE_REGEX.matcher(requestUrl).matches();
    }

    public static boolean isHelloUrl(final String requestUrl) {
        return requestUrl.equals("/");
    }

    private static boolean isRegisterUrl(final String requestUrl) {
        return requestUrl.equals("/register");
    }

    public static boolean isLoginUrl(final String requestUrl) {
        return requestUrl.startsWith("/login");
    }

    public Controller getController() {
        return controller;
    }
}
