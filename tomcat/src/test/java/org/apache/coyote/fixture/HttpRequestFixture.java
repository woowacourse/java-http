package org.apache.coyote.fixture;

import java.util.List;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.body.RequestBody;
import org.apache.coyote.request.header.RequestHeader;

public class HttpRequestFixture {

    public static final HttpRequest NO_EXIST_METHOD_REQUEST = new HttpRequest(
            RequestLineFixture.NOT_EXIST_METHOD_REQUESTLINE,
            RequestHeaderFixture.DEFAULT,
            new RequestBody(null)
    );

    public static final HttpRequest GET_DEFAULT_PATH_REQUEST = new HttpRequest(
            RequestLineFixture.DEFAULT_GET,
            RequestHeaderFixture.DEFAULT,
            new RequestBody(null)
    );

    public static final HttpRequest POST_DEFAULT_PATH_REQUEST = new HttpRequest(
            RequestLineFixture.DEFAULT_POST,
            RequestHeaderFixture.DEFAULT,
            new RequestBody(null)
    );

    public static final HttpRequest GET_REGISTER_PATH_REQUEST = new HttpRequest(
            RequestLineFixture.REGISTER_GET,
            RequestHeaderFixture.REGISTER_GET_DEFAULT,
            new RequestBody(null)
    );

    public static final HttpRequest POST_REGISTER_PATH_REQUEST = new HttpRequest(
            RequestLineFixture.REGISTER_POST,
            RequestHeaderFixture.REGISTER_POST_DEFAULT,
            RequestBodyFixture.REGISTERED_USER_BODY
    );

    public static final HttpRequest POST_NEW_USER_REGISTER_PATH_REQUEST = new HttpRequest(
            RequestLineFixture.REGISTER_POST,
            RequestHeaderFixture.REGISTER_POST_DEFAULT,
            RequestBodyFixture.NEW_USER_BODY
    );

    public static final HttpRequest GET_LOGIN_PATH_NO_COOKIE_REQUEST = new HttpRequest(
            RequestLineFixture.LOGIN_GET,
            RequestHeaderFixture.LOGIN_GET_DEFAULT,
            new RequestBody(null)
    );

    public static final HttpRequest POST_LOGIN_PATH_REQUEST = new HttpRequest(
            RequestLineFixture.LOGIN_POST,
            RequestHeaderFixture.LOGIN_POST_DEFAULT,
            RequestBodyFixture.NOT_REGISTER_LOGIN_USER_BODY
    );

    public static final HttpRequest POST_LOGGED_IN_USER_PATH_REQUEST = new HttpRequest(
            RequestLineFixture.LOGIN_POST,
            RequestHeaderFixture.LOGIN_POST_DEFAULT,
            RequestBodyFixture.REGISTERED_LOGIN_USER_BODY
    );

    public static final HttpRequest POST_STATIC_MAIN_PATH_REQUEST = new HttpRequest(
            RequestLineFixture.MAIN_RESOURCE_POST,
            RequestHeaderFixture.RESOURCE_DEFAULT,
            new RequestBody(null)
    );

    public static final HttpRequest GET_STATIC_MAIN_PATH_REQUEST = new HttpRequest(
            RequestLineFixture.MAIN_RESOURCE_GET,
            RequestHeaderFixture.RESOURCE_DEFAULT,
            new RequestBody(null)
    );

    public static HttpRequest POST_LOGGED_IN_USER_WITH_COOKIE(String cookie) {
        return new HttpRequest(
                RequestLineFixture.LOGIN_POST,
                new RequestHeader(List.of("Host: localhost:8080", "Connection: keep-alive",
                        "Accept: text/html,application/xhtml+xml", "Content-Length: 37",
                        "Content-Type: application/x-www-form-urlencoded", cookie)),
                RequestBodyFixture.REGISTERED_LOGIN_USER_BODY
        );
    }
}
