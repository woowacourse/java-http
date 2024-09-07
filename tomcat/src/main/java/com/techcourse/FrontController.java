package com.techcourse;

import com.techcourse.service.LoginService;
import com.techcourse.model.User;
import java.net.URL;
import java.util.Map;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrontController {

    private static final Logger log = LoggerFactory.getLogger(FrontController.class);

    public static void service(HttpRequest request, HttpResponse response) {
        String path = request.getPath();
        if (path.endsWith("html") || path.endsWith("css") || path.endsWith("js")) {
            handleStaticFile(request, response);
            return;
        }
        if (path.equals("/")) {
            handleIndex(response);
            return;
        }
        if (path.equals("/login")) {
            handleLogin(request, response);
            return;
        }
        response.setStatus(HttpStatus.NOT_FOUND);
    }

    private static void handleStaticFile(HttpRequest request, HttpResponse response) {
        String url = request.getUri();
        ContentType contentType = ContentType.findByUrl(url);
        String resourceUrl = getResourceUrl(contentType, url);
        URL resource = Http11Processor.class.getClassLoader().getResource(resourceUrl);
        if (resource == null) { // TODO 에러 처리 통일
            log.warn("Bad Request resourceUrl = {}", resourceUrl);
            response.setStatus(HttpStatus.NOT_FOUND);
            return;
        }
        response.setStatus(HttpStatus.OK);
    }

    private static String getResourceUrl(ContentType contentType, String rawUrl) {
        if (rawUrl.contains(".")) {
            return "static" + rawUrl;
        }
        return "static" + rawUrl + "." + contentType.getType();
    }

    private static void handleIndex(HttpResponse response) {
        response.setStatus(HttpStatus.OK);
    }

    private static void handleLogin(HttpRequest request, HttpResponse response) { // TODO Adviser 도입
        try {
            User user = LoginService.login(request.findQuery("account"), request.findQuery("password"));
            response.setStatus(HttpStatus.FOUND);
            log.info("Login Success = {}", user);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED);
            log.warn(e.getMessage());
        }
    }
}
