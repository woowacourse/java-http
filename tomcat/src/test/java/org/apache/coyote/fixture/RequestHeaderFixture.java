package org.apache.coyote.fixture;

import java.util.List;
import org.apache.coyote.request.header.RequestHeader;

public class RequestHeaderFixture {

    public static final RequestHeader DEFAULT = new RequestHeader(
            List.of("Host: localhost:8080",
                    "Connection: keep-alive",
                    "Accept: */*")
    );

    public static final RequestHeader REGISTER_POST_DEFAULT = new RequestHeader(
            List.of("Host: localhost:8080",
                    "Connection: keep-alive",
                    "Content-Length: 80",
                    "Content-Type: application/x-www-form-urlencoded",
                    "Accept: */*")
    );

    public static final RequestHeader REGISTER_GET_DEFAULT = new RequestHeader(
            List.of("Host: localhost:8080",
                    "Connection: keep-alive",
                    "Content-Length: 80",
                    "Content-Type: text/html,application/xhtml+xml",
                    "Accept: text/html")
    );

    public static final RequestHeader LOGIN_POST_DEFAULT = new RequestHeader(
            List.of("Host: localhost:8080",
                    "Connection: keep-alive",
                    "Accept: text/html,application/xhtml+xml",
                    "Content-Length: 37",
                    "Content-Type: application/x-www-form-urlencoded")
    );

    public static final RequestHeader LOGIN_GET_DEFAULT = new RequestHeader(
            List.of("Host: localhost:8080",
                    "Connection: keep-alive",
                    "Accept: text/html,application/xhtml+xml",
                    "Content-Length: 37",
                    "Content-Type: text/html,application/xhtml+xml")
    );

    public static final RequestHeader RESOURCE_DEFAULT = new RequestHeader(
            List.of("Host: localhost:8080",
                    "Connection: keep-alive",
                    "Accept: text/html,application/xhtml+xml")
    );

    public static final RequestHeader COOKIE_RESOURCE_DEFAULT = new RequestHeader(
            List.of("Host: localhost:8080",
                    "Connection: keep-alive",
                    "Accept: text/html,application/xhtml+xml",
                    "Cookie: yummy_cookie=choco; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46")
    );
}
