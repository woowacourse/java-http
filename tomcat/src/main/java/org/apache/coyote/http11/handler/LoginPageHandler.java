package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.enums.HttpStatus;

import java.util.Map;

public class LoginPageHandler implements RequestHandler {
    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        HttpCookie httpCookie = new HttpCookie();
        final Map<String, String> parsedCookie = httpCookie.parseCookie(
                httpRequest.headers().getOrDefault("cookie", ""));

        if (parsedCookie.containsKey("JSESSIONID")) {
            return new HttpResponse("/index.html", HttpStatus.FOUND);
        }

        return new HttpResponse("/login", HttpStatus.OK);
    }
}
