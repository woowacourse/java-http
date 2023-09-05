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
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.RequestBody;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.ResponseBody;

public class LoginHandler extends Handler {

    public HttpResponse handle(HttpRequest httpRequest) throws IOException {
        HttpMethod httpMethod = httpRequest.getMethod();
        if (httpMethod.isEqualTo(HttpMethod.GET)) {
            return responseWhenHttpMethodIsGet(httpRequest);
        }
        if (httpMethod.isEqualTo(HttpMethod.POST)) {
            return responseWhenHttpMethodIsPost(httpRequest);
        }

        throw new IllegalArgumentException();
    }

    private HttpResponse responseWhenHttpMethodIsGet(HttpRequest httpRequest) throws IOException {
        Headers requestHeaders = httpRequest.getHeaders();
        Cookie cookie = requestHeaders.getCookie();

        if (cookie.hasKey("JSESSIONID")) {
            return responseForLoggedIn(httpRequest);
        }
        return responseForNotLoggedIn(httpRequest);
    }

    private HttpResponse responseForLoggedIn(HttpRequest httpRequest) {
        String absolutePath = INDEX_PAGE.path();
        Headers headers = Headers.fromMap(Map.of(
                LOCATION, absolutePath
        ));

        return new HttpResponse(httpRequest.getHttpVersion(), HttpStatus.FOUND,
                headers, ResponseBody.ofEmpty());
    }

    private HttpResponse responseForNotLoggedIn(HttpRequest httpRequest) throws IOException {
        String absolutePath = LOGIN_PAGE.path();
        String resource = findResourceWithPath(absolutePath);
        Headers headers = Headers.fromMap(Map.of(
                CONTENT_TYPE, ContentTypeParser.parse(absolutePath),
                CONTENT_LENGTH, String.valueOf(resource.getBytes().length)
        ));
        ResponseBody responseBody = new ResponseBody(resource);

        return new HttpResponse(httpRequest.getHttpVersion(), HttpStatus.OK,
                headers, responseBody);
    }

    private HttpResponse responseWhenHttpMethodIsPost(HttpRequest httpRequest) {
        RequestBody requestBody = httpRequest.getBody();
        String account = requestBody.get("account");
        String password = requestBody.get("password");

        if (InMemoryUserRepository.hasSameCredential(account, password)) {
            return responseWhenLoginSuccess(httpRequest);
        }
        return responseWhenLoginFail(httpRequest);
    }

    private HttpResponse responseWhenLoginSuccess(HttpRequest httpRequest) {
        UUID sessionId = saveSession(httpRequest);

        String absolutePath = INDEX_PAGE.path();
        Headers headers = Headers.fromMap(Map.of(
                SET_COOKIE, "JSESSIONID=" + sessionId,
                LOCATION, absolutePath
        ));

        return new HttpResponse(httpRequest.getHttpVersion(), HttpStatus.FOUND,
                headers, ResponseBody.ofEmpty());
    }

    private HttpResponse responseWhenLoginFail(HttpRequest httpRequest) {
        String absolutePath = UNAUTHORIZED_PAGE.path();

        Headers headers = Headers.fromMap(Map.of(
                LOCATION, absolutePath
        ));

        return new HttpResponse(httpRequest.getHttpVersion(), HttpStatus.FOUND,
                headers, ResponseBody.ofEmpty());
    }

    private UUID saveSession(HttpRequest httpRequest) {
        RequestBody body = httpRequest.getBody();
        String account = body.get("account");
        String password = body.get("password");

        Long userId = InMemoryUserRepository.getIdByCredentials(account, password);

        return InMemorySessionRepository.save(userId);
    }
}
