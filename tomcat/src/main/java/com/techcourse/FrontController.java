package com.techcourse;

import com.techcourse.service.LoginService;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrontController {

    private static final Logger log = LoggerFactory.getLogger(FrontController.class);

    public static void service(HttpRequest request) {
        String url = request.getUrl();
        if (url.equals("/login")) {
            handleLogin(request);
        }
    }

    private static void handleLogin(HttpRequest request) {
        if (request.getUrl().equals("/login")) {
            Map<String, String> queries = request.getQueries();
            User user = LoginService.login(queries.get("account"), queries.get("password"));
            log.info("Login Success = {}", user);
        }
    }
}
