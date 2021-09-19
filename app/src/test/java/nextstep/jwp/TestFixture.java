package nextstep.jwp;

import java.util.HashMap;
import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.request.HttpHeaders;
import nextstep.jwp.http.request.HttpMethod;
import nextstep.jwp.http.request.HttpRequest;

public class TestFixture {

    public static HttpRequest getHttpRequest(HttpMethod httpMethod, String url) {
        return new HttpRequest(
                httpMethod,
                url,
                "HTTP/1.1",
                new HttpHeaders(new HashMap<>(), new HttpCookie())
        );
    }

    public static HttpRequest getHttpRequest(HttpMethod httpMethod, String url, String body) {
        return new HttpRequest(
                httpMethod,
                url,
                "HTTP/1.1",
                new HttpHeaders(new HashMap<>(), new HttpCookie()),
                body
        );
    }

    public static HttpRequest getHttpRequest(HttpMethod httpMethod, String url,
            HttpCookie httpCookie) {
        return new HttpRequest(
                httpMethod,
                url,
                "HTTP/1.1",
                new HttpHeaders(new HashMap<>(), httpCookie)
        );
    }

    public static HttpRequest getHttpRequest(HttpMethod httpMethod, String url, String body,
            HttpCookie httpCookie) {
        return new HttpRequest(
                httpMethod,
                url,
                "HTTP/1.1",
                new HttpHeaders(new HashMap<>(), httpCookie),
                body
        );
    }
}
