package nextstep.jwp;

import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.entity.HttpBody;
import nextstep.jwp.http.entity.HttpCookie;
import nextstep.jwp.http.entity.HttpHeaders;
import nextstep.jwp.http.entity.HttpMethod;
import nextstep.jwp.http.entity.HttpSession;
import nextstep.jwp.http.entity.HttpUri;
import nextstep.jwp.http.entity.HttpVersion;

public class Fixture {
    public static HttpRequest httpRequest(String method, String uri) {
        return new HttpRequest(HttpMethod.of(method), HttpUri.of(uri), HttpVersion.HTTP_1_1, new HttpHeaders(),
                HttpBody.empty(), HttpCookie.empty());
    }

    public static HttpRequest httpRequest(String method, String uri, String body) {
        return new HttpRequest(HttpMethod.of(method), HttpUri.of(uri), HttpVersion.HTTP_1_1, new HttpHeaders(),
                HttpBody.of(body), HttpCookie.empty());
    }

    public static HttpRequest httpRequest(String method, String uri, HttpSession httpSession) {
        return new HttpRequest(HttpMethod.of(method), HttpUri.of(uri), HttpVersion.HTTP_1_1, new HttpHeaders(),
                HttpBody.empty(), HttpCookie.of(httpSession));
    }
}
