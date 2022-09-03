package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.handler.StaticHandler;
import org.apache.coyote.http11.HttpRequest;

public class FrontController {

    private static final String LOGIN_PATH = "/login";
    private static final String NOT_FOUND_PATH = "/404.html";

    public static String handle(HttpRequest request) throws IOException {
        String path = request.path();

        if (isStaticRequest(path)) {
            return StaticHandler.handleStatic(path);
        }
        if (isLoginRequest(path)) {
            return LoginController.handle(request);
        }
        return StaticHandler.handleStatic(NOT_FOUND_PATH);
    }

    private static boolean isStaticRequest(String path) {
        return path.contains(".");
    }

    private static boolean isLoginRequest(String path) {
        return path.equals(LOGIN_PATH);
    }
}
