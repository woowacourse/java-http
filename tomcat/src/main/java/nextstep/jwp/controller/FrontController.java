package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.handler.StaticHandler;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class FrontController {

    private static final String LOGIN_PATH = "/login";
    private static final String NOT_FOUND_PATH = "/404.html";

    // TODO: 리다이렉트 구현
    public static HttpResponse handle(HttpRequest request) throws IOException {
        String path = request.path();
        HttpResponse response = HttpResponse.ok();

        if (isStaticRequest(path)) {
            return StaticHandler.handle(path, response);
        }
        if (isLoginRequest(path)) {
            return LoginController.handle(request, response);
        }
        return StaticHandler.handle(NOT_FOUND_PATH, response);
    }

    private static boolean isStaticRequest(String path) {
        return path.contains(".");
    }

    private static boolean isLoginRequest(String path) {
        return path.equals(LOGIN_PATH);
    }
}
