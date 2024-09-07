package org.apache.coyote.http11.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.service.LoginService;


public class LoginController implements Controller {

    private final LoginService loginService = new LoginService();

    @Override
    public boolean canHandle(String url) {
        return url.contains("login");
    }

    @Override
    public Map<String, String> handle(HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.getRequestLine();
        if (requestLine.isQueryStringRequest()) {
            return checkLogin(requestLine);
        }

        return resolveResponse(200, "/login.html");
    }

    private Map<String, String> checkLogin(RequestLine requestLine) {
        Map<String, String> parameters = requestLine.getParameters();
        loginService.checkLogin(parameters.get("account"), parameters.get("password"));
        return resolveResponse(302, "/index.html");
    }

    private Map<String, String> resolveResponse(int statusCode, String viewUrl){
        Map<String, String> map = new HashMap<>();
        map.put("statusCode", String.valueOf(statusCode));
        map.put("url", viewUrl);
        return map;
    }
}
