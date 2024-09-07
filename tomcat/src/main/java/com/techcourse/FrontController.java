package com.techcourse;

import com.techcourse.exception.NotFoundException;
import com.techcourse.model.User;
import com.techcourse.service.LoginService;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.view.ViewResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrontController {

    private static final Logger log = LoggerFactory.getLogger(FrontController.class);

    public static void service(HttpRequest request, HttpResponse response) {
        try {
            String path = request.getPath();
            if (path.endsWith("html") || path.endsWith("css") || path.endsWith("js")) {
                handleStaticFile(request, response);
            }
            if (path.equals("/")) {
                handleIndex(response);
            }
            if (path.equals("/login")) {
                handleLogin(request, response);
            }
            response.setHeaders(HttpHeaders.of(response.getView(), ContentType.findByPath(request.getPath())));
        } catch (NotFoundException e) {
            log.warn("[NotFoundException] " + e.getMessage());
            response.setView(ViewResolver.getView("404.html"));
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setHeaders(HttpHeaders.of(response.getView(), ContentType.HTML));
        }
    }

    private static void handleStaticFile(HttpRequest request, HttpResponse response) {
        response.setView(ViewResolver.getView(request.getPath()));
        response.setStatus(HttpStatus.OK);
    }

    private static void handleIndex(HttpResponse response) {
        response.setView(ViewResolver.getView("index.html"));
        response.setStatus(HttpStatus.OK);
    }

    private static void handleLogin(HttpRequest request, HttpResponse response) { // TODO Adviser 도입
        try {
            User user = LoginService.login(request.findQuery("account"), request.findQuery("password"));
            response.setView(ViewResolver.getView("login.html"));
            response.setStatus(HttpStatus.FOUND);
            log.info("Login Success = {}", user);
        } catch (IllegalArgumentException e) {
            response.setView(ViewResolver.getView("401.html"));
            response.setStatus(HttpStatus.UNAUTHORIZED);
            log.warn(e.getMessage());
        }
    }
}
