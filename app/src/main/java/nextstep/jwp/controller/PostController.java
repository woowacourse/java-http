package nextstep.jwp.controller;

import java.io.OutputStream;
import nextstep.jwp.http.request.HttpRequest;

public class PostController {

    private PostController() {

    }

    public static void run(String requestURI, OutputStream outputStream, ClassLoader classLoader,
        HttpRequest httpRequest) {

        if ("/register".equals(requestURI)) {
            register(outputStream, classLoader);
        }
    }

    private static void register(OutputStream outputStream, ClassLoader classLoader) {

    }
}
