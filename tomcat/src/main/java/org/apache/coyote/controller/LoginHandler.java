package org.apache.coyote.controller;

import com.techcourse.model.User;
import com.techcourse.service.UserService;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.coyote.ContentTypeSearcher;
import org.apache.coyote.FileManager;
import org.apache.coyote.http11.Http11Cookie;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;

public class LoginHandler {

    private static final String STATIC_ROOT = "static";

    public static Http11Response getResponse(Http11Request request, SessionManager sessionManager) {
        HttpStatusCode statusCode;
        Http11Response response;

        if (Objects.equals(request.getRequestMethod(), "GET")) {
            return processGetMethod(request, sessionManager);
        }

        if (Objects.equals(request.getRequestMethod(), "POST")) {
            return processPostMethod(request, sessionManager);
        }

        statusCode = HttpStatusCode.NOTFOUND;
        String body = "404 Not Found";
        response = new Http11Response(statusCode,
                "text/html; charset=utf-8",
                body.getBytes(StandardCharsets.UTF_8),
                null,
                null);

        return response;
    }

    private static Http11Response processGetMethod(Http11Request request, SessionManager sessionManager) {
        Http11Response response;
        HttpStatusCode statusCode;
        String path = request.getPath();
        FileManager fileManager;

        // 이미 세션이 존재한다면 바로 index.html로 리다이렉트
        if (isAlreadyLogined(request, sessionManager)) {
            response = new Http11Response(
                    HttpStatusCode.FOUND,
                    null,
                    null,
                    "/index.html",
                    null);

            return response;
        }

        try {
            fileManager = new FileManager(STATIC_ROOT + "/login.html", path);
        } catch (Exception e) {
            statusCode = HttpStatusCode.INTERNAL_SERVER_ERROR;
            String body = "500 Internal Server Error";
            response = new Http11Response(statusCode,
                    "text/html; charset=utf-8",
                    body.getBytes(StandardCharsets.UTF_8),
                    null,
                    null);

            return response;
        }

        String contentType = ContentTypeSearcher.getContentTypeBy("/login.html");
        statusCode = HttpStatusCode.OK;
        response = new Http11Response(
                statusCode,
                contentType,
                fileManager.getContent(),
                null,
                null);

        return response;
    }

    private static boolean isAlreadyLogined(Http11Request request, SessionManager sessionManager) {
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

    private static Http11Response processPostMethod(Http11Request request, SessionManager sessionManager) {
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
            User user = UserService.findUser(account, password);
            Http11Cookie cookies = request.getCookies();

            HttpStatusCode statusCode = HttpStatusCode.FOUND;

            if (!cookies.isCookieExist("JSESSIONID")) {
                UUID uuid = UUID.randomUUID();

                Session session = new Session(uuid.toString());
                session.setAttribute("user", user);
                sessionManager.add(session);

                Http11Response response = new Http11Response(
                        statusCode,
                        null,
                        null,
                        "/index.html",
                        uuid);

                return response;
            }
            Http11Response response = new Http11Response(
                    statusCode,
                    null,
                    null,
                    "/index.html",
                    null);

            return response;
        } catch (IllegalArgumentException e) {
            // 회원이 없거나 못찾은 경우
            HttpStatusCode statusCode = HttpStatusCode.FOUND;
            Http11Response response = new Http11Response(
                    statusCode,
                    null,
                    null,
                    "/401.html",
                    null);

            return response;
        }
    }
}
