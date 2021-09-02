package nextstep.jwp.http;

import java.util.UUID;

public class CookieHandler {
    public static void handle(HttpRequest httpRequest, HttpResponse httpResponse) {
        String cookieName = "JSESSIONID";
        if (!httpRequest.containsCookie(cookieName)) {
            httpResponse.addHeader("Set-Cookie", cookieName + "=" + UUID.randomUUID());
        }
    }
}
