package org.apache.coyote.http11.controller;

import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.service.LoginService;
import org.apache.coyote.http11.session.SessionManager;

public class LoginController implements Controller {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String LOCATION_HEADER = "Location";

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    public Response<String> handle(Request request) {
        if (SessionManager.loggedIn(request)) {
            return Response.status(302)
                .addHeader(LOCATION_HEADER, "/index.html")
                .build();
        }

        Optional<String> loginSession = login(request);
        if (loginSession.isPresent()) {
            return Response.status(302)
                .addHeader(LOCATION_HEADER, "/index.html")
                .addHeader("Set-Cookie", "JSESSIONID" + "=" + loginSession.get())
                .build();
        }

        return Response.status(302)
            .addHeader(LOCATION_HEADER, "/401.html")
            .build();
    }

    private Optional<String> login(Request request) {
        Map<String, String> bodyData = BodyExtractor.convertBody(request.getResponseBody());
        String account = bodyData.get(ACCOUNT);
        String password = bodyData.get(PASSWORD);
        if (account != null && password != null) {
            return loginService.login(account, password);
        }
        return Optional.empty();
    }
}
