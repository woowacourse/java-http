package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.PagePathMapper.*;
import static org.apache.coyote.http11.message.HttpHeaders.*;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import nextstep.jwp.db.InMemorySessionRepository;
import nextstep.jwp.db.InMemoryUserRepository;
import org.apache.coyote.http11.ContentTypeParser;
import org.apache.coyote.http11.message.Cookie;
import org.apache.coyote.http11.message.Headers;
import org.apache.coyote.http11.message.HttpMethod;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.request.Request;
import org.apache.coyote.http11.message.request.RequestBody;
import org.apache.coyote.http11.message.response.Response;
import org.apache.coyote.http11.message.response.ResponseBody;

public class LoginHandler extends Handler {

    public Response handle(Request request) throws IOException {
        HttpMethod httpMethod = request.getMethod();
        if (httpMethod.isEqualTo(HttpMethod.GET)) {
            return responseWhenHttpMethodIsGet(request);
        }
        if (httpMethod.isEqualTo(HttpMethod.POST)) {
            return responseWhenHttpMethodIsPost(request);
        }

        throw new IllegalArgumentException();
    }

    private Response responseWhenHttpMethodIsGet(Request request) throws IOException {
        Headers requestHeaders = request.getHeaders();
        Cookie cookie = requestHeaders.getCookie();

        if (cookie.hasKey("JSESSIONID")) {
            return responseForLoggedIn(request);
        }
        return responseForNotLoggedIn(request);
    }

    private Response responseForLoggedIn(Request request) {
        String absolutePath = INDEX_PAGE.path();
        Headers headers = Headers.fromMap(Map.of(
                LOCATION, absolutePath
        ));

        return Response.from(request.getHttpVersion(), HttpStatus.FOUND,
                headers, ResponseBody.ofEmpty());
    }

    private Response responseForNotLoggedIn(Request request) throws IOException {
        String absolutePath = LOGIN_PAGE.path();
        String resource = findResourceWithPath(absolutePath);
        Headers headers = Headers.fromMap(Map.of(
                CONTENT_TYPE, ContentTypeParser.parse(absolutePath),
                CONTENT_LENGTH, String.valueOf(resource.getBytes().length)
        ));
        ResponseBody responseBody = new ResponseBody(resource);

        return Response.from(request.getHttpVersion(), HttpStatus.OK,
                headers, responseBody);
    }

    private Response responseWhenHttpMethodIsPost(Request request) {
        RequestBody requestBody = request.getBody();
        String account = requestBody.get("account");
        String password = requestBody.get("password");

        if (InMemoryUserRepository.hasSameCredential(account, password)) {
            return responseWhenLoginSuccess(request);
        }
        return responseWhenLoginFail(request);
    }

    private Response responseWhenLoginSuccess(Request request) {
        UUID sessionId = saveSession(request);

        String absolutePath = INDEX_PAGE.path();
        Headers headers = Headers.fromMap(Map.of(
                SET_COOKIE, "JSESSIONID=" + sessionId,
                LOCATION, absolutePath
        ));

        return Response.from(request.getHttpVersion(), HttpStatus.FOUND,
                headers, ResponseBody.ofEmpty());
    }

    private Response responseWhenLoginFail(Request request) {
        String absolutePath = UNAUTHORIZED_PAGE.path();

        Headers headers = Headers.fromMap(Map.of(
                LOCATION, absolutePath
        ));

        return Response.from(request.getHttpVersion(), HttpStatus.FOUND,
                headers, ResponseBody.ofEmpty());
    }

    private UUID saveSession(Request request) {
        RequestBody body = request.getBody();
        String account = body.get("account");
        String password = body.get("password");

        Long userId = InMemoryUserRepository.getIdByCredentials(account, password);

        return InMemorySessionRepository.save(userId);
    }
}
