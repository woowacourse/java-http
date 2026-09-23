package org.apache.coyote.http11.handler;

import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.enums.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class LoginPageHandler implements RequestHandler {
    private static final Logger log = LoggerFactory.getLogger(LoginPageHandler.class);

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        Map<String, String> headers = new HashMap<>();

        HttpCookie cookie = new HttpCookie(httpRequest.headers().getOrDefault("cookie", ""));
        try{
            cookie.getSessionId();
            headers.put("Location", "/index.html");
            return new HttpResponse("/index.html", HttpStatus.FOUND, headers);
        }
        catch (IllegalArgumentException e){
            return new HttpResponse("/login", HttpStatus.OK, headers);
        }
    }
}
