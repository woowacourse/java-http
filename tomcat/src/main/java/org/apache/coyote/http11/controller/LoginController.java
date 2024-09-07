package org.apache.coyote.http11.controller;

import java.util.Map;
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
            checkLogin2(requestLine);
        }

        return new ResponseBuilder()
                .statusCode(200)
                .viewUrl("/login.html")
                .build();
    }

    private HttpResponse checkLogin2(RequestLine requestLine) {
        Map<String, String> parameters = requestLine.getParameters();
        loginService.checkLogin(parameters.get("account"), parameters.get("password"));
        return new ResponseBuilder()
                .statusCode(302)
                .location("/index.html")
                .build();
    }
}
