package org.apache.coyote.controller;

import com.techcourse.model.User;
import com.techcourse.service.UserService;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.coyote.ContentTypeSearcher;
import org.apache.coyote.FileManager;
import org.apache.coyote.http11.Http11Cookie;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final String STATIC_ROOT = "static";
    private static final SessionManager sessionManager = SessionManager.getInstance();
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doGet(Http11Request request, Http11Response response) {
        String path = request.getPath();
        FileManager fileManager;
        // 이미 세션이 존재한다면 바로 index.html로 리다이렉트
        if (isAlreadyLogined(request)) {
            response.status(HttpStatus.FOUND);
            response.location("/index.html");

            return;
        }

        try {
            fileManager = new FileManager(STATIC_ROOT + "/login.html", path);
        } catch (Exception e) {
            String body = "500 Internal Server Error";
            response.status(HttpStatus.INTERNAL_SERVER_ERROR);
            response.contentType("text/html; charset=utf-8");
            response.body(body.getBytes());

            return;
        }
        log.info("file found");
        String contentType = ContentTypeSearcher.getContentTypeBy("/login.html");
        response.status(HttpStatus.OK);
        response.contentType(contentType);
        response.body(fileManager.getContent());
    }

    private static boolean isAlreadyLogined(Http11Request request) {
        Http11Cookie cookies = request.getCookies();

        if (cookies == null) {
            return false;
        }

        String sessionId = cookies.getCookie("JSESSIONID");

        if (sessionId == null) {
            return false;
        }

        try {
            return sessionManager.findSession(sessionId) != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void doPost(Http11Request request, Http11Response response) {
        String body = request.getBody();
        Map<String, String> bodies = Arrays.stream(body.split("&"))
                .map(set -> set.split("=", 2))
                .collect(Collectors.toMap(
                        arr -> arr[0],
                        arr -> arr[1]
                ));

        String account = bodies.get("account");
        String password = bodies.get("password");

        try {
            User loginedUser = UserService.login(account, password);
            Http11Cookie cookies = request.getCookies();

            HttpStatus statusCode = HttpStatus.FOUND;

            if (!cookies.isCookieExist("JSESSIONID")) {
                UUID uuid = UUID.randomUUID();

                Session session = new Session(uuid.toString());
                session.setAttribute("user", loginedUser);
                sessionManager.add(session);

                response.status(statusCode);
                response.location("/index.html");
                response.cookie(uuid.toString());

                return;
            }
            response.status(statusCode);
            response.location("/index.html");
        } catch (IllegalArgumentException e) {
            // 회원이 없거나 못찾은 경우
            HttpStatus statusCode = HttpStatus.FOUND;
            response.status(statusCode);
            response.location("/401.html");
        }
    }
}
