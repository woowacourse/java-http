package org.apache.coyote.http11.controller;

import java.util.Map;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.auth.Session;
import org.apache.coyote.http11.auth.SessionGenerator;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBuilder;
import org.apache.coyote.http11.service.LoginService;


public class LoginController implements Controller {

    private final LoginService loginService = new LoginService();

    @Override
    public boolean canHandle(String url) {
        return url.contains("login");
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.getRequestLine();
        if (requestLine.isQueryStringRequest()) {
            checkLogin(requestLine);
        }

        return new ResponseBuilder()
                .statusCode(HttpStatusCode.OK_200)
                .viewUrl("/login.html")
                .build();
    }

    private HttpResponse checkLogin(RequestLine requestLine) {
        Map<String, String> parameters = requestLine.getParameters();
        loginService.checkLogin(parameters.get("account"), parameters.get("password"));

        return new ResponseBuilder()
                .statusCode(HttpStatusCode.FOUND_302)
                .location("/index.html")
                .setCookie(makeSessionCookie())
                .build();
    }

    private static Cookie makeSessionCookie() {
        Session session = SessionGenerator.generate();
        return new Cookie(Map.of("JSESSIONID", session.getValue()));
    }
}
