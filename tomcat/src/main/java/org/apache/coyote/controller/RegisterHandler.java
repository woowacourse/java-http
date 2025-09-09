package org.apache.coyote.controller;

import com.techcourse.service.UserService;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.coyote.ContentTypeSearcher;
import org.apache.coyote.FileManager;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.session.SessionManager;

public class RegisterHandler {

    private static final String STATIC_ROOT = "static";

    public static Http11Response getResponse(Http11Request request, SessionManager sessionManager) {
        if (Objects.equals(request.getRequestMethod(), "GET")) {
            return processGetMethod(request);
        }

        if (Objects.equals(request.getRequestMethod(), "POST")) {
            return processPostMethod(request);
        }

        HttpStatusCode httpStatusCode = HttpStatusCode.NOTFOUND;
        Http11Response response = new Http11Response(
                httpStatusCode,
                null,
                null,
                null,
                null);

        return response;
    }

    private static Http11Response processGetMethod(Http11Request request) {

        Http11Response response;
        HttpStatusCode statusCode;
        String path = request.getPath();
        FileManager fileManager;

        try {
            fileManager = new FileManager(STATIC_ROOT + "/register.html", path);
        } catch (Exception e) {
            statusCode = HttpStatusCode.INTERNAL_SERVER_ERROR;
            String body = "500 Internal Server Error";
            response = new Http11Response(
                    statusCode,
                    "text/html; charset=utf-8",
                    body.getBytes(StandardCharsets.UTF_8),
                    null,
                    null);

            return response;
        }

        String contentType = ContentTypeSearcher.getContentTypeBy("/register.html");
        statusCode = HttpStatusCode.OK;
        response = new Http11Response(
                statusCode,
                contentType,
                fileManager.getContent(),
                null,
                null);

        return response;
    }

    private static Http11Response processPostMethod(Http11Request request) {
        String body = request.getBody();
        Map<String, String> map = Arrays.stream(body.split("&"))
                .map(set -> set.split("=", 2))
                .collect(Collectors.toMap(
                        arr -> arr[0],
                        arr -> arr[1]
                ));

        String account = map.get("account");
        String email = map.get("email");
        String password = map.get("password");

        UserService.createUser(account, password, email);

        return new Http11Response(HttpStatusCode.FOUND,
                null,
                null,
                "/index.html",
                null);
    }
}
