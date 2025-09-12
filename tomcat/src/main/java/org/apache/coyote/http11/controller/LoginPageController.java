package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request_response.HttpMethod;
import org.apache.coyote.http11.request_response.HttpStatus;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.util.StaticFileReader;
import org.apache.coyote.http11.request_response.request.HttpRequest;
import org.apache.coyote.http11.request_response.response.HttpResponse;

public class LoginPageController implements Controller {

    @Override
    public boolean supports(HttpRequest request) {
        return request.getRequestMethod().equals(HttpMethod.GET) && request.getUriPath().equals("/login");
    }

    @Override
    public HttpResponse service(HttpRequest request) {
        Session session = request.getSession(false);
        if (session != null) {
            Object user = session.getAttribute("user");
            if (user != null) {
                return HttpResponse.builder()
                    .status(HttpStatus.Found)
                    .header("Location", "/index.html")
                    .body("")
                    .build();
            }
        }
        String responseBody = new StaticFileReader().readStaticFile("/login.html");
        return HttpResponse.builder()
            .status(HttpStatus.OK)
            .contentType("text/html;charset=utf-8")
            .body(responseBody)
            .build();
    }
}
