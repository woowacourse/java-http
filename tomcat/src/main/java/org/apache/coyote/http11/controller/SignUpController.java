package org.apache.coyote.http11.controller;

import java.util.Map;
import org.apache.coyote.http11.exception.MemberAlreadyExistsException;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.service.LoginService;
import org.apache.coyote.http11.session.SessionManager;

public class SignUpController implements Controller {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String LOCATION_HEADER = "Location";

    private final LoginService loginService;

    public SignUpController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    public Response handle(Request request) {
        try {
            if (SessionManager.loggedIn(request)) {
                return Response.status(302)
                    .addHeader(LOCATION_HEADER, "/index.html")
                    .build();
            }
            String loginSession = signUp(request);
            return Response.status(302)
                .addHeader(LOCATION_HEADER, "/index.html")
                .addHeader("Set-Cookie", "JSESSIONID" + "=" + loginSession)
                .build();
        } catch (MemberAlreadyExistsException e) {
            return Response.status(302)
                .addHeader(LOCATION_HEADER, "/register.html")
                .build();
        }
    }

    private String signUp(Request request) {
        Map<String, String> bodyData = BodyExtractor.convertBody(request.getResponseBody());
        String account = bodyData.get(ACCOUNT);
        String password = bodyData.get(PASSWORD);
        String email = bodyData.get(EMAIL);
        return loginService.signUp(account, password, email);
    }
}
