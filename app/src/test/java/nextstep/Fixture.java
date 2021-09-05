package nextstep;

public class Fixture {
    public static String makeGetRequest(String uri) {
        return String.join("\r\n",
                "GET " + uri + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
    }

    public static String makeGetRequestWithCookie(String uri) {
        return String.join("\r\n",
                "GET " + uri + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "",
                "");
    }

    public static String makeGetRequestWithCookieWithoutSessionId(String uri) {
        return String.join("\r\n",
                "GET " + uri + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; ",
                "",
                "");
    }

    public static String makePostRequest(String uri, String body) {
        return String.join("\r\n",
                "POST " + uri + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length,
                "",
                body,
                "",
                "");
    }
}
