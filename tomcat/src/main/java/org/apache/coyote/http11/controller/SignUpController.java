package org.apache.coyote.http11.controller;

import java.util.Map;
import org.apache.coyote.http11.controller.util.BodyExtractor;
import org.apache.coyote.http11.exception.MemberAlreadyExistsException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.service.LoginService;
import org.apache.coyote.http11.session.SessionManager;

public class SignUpController extends AbstractController {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String LOCATION_HEADER = "Location";
    private static final String INDEX_PAGE = "/index.html";

    private final LoginService loginService;

    public SignUpController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    public ResponseEntity<? extends Object> doGet(HttpRequest httpRequest) {
        if (SessionManager.loggedIn(httpRequest)) {
            return ResponseEntity.status(302)
                .addHeader(LOCATION_HEADER, INDEX_PAGE)
                .build();
        }
        ResponseEntity<Object> responseEntity = ResponseEntity.status(200).build();
        responseEntity.responseView("/register.html");
        return responseEntity;
    }

    @Override
    public ResponseEntity<? extends Object> doPost(HttpRequest httpRequest) {
        try {
            if (SessionManager.loggedIn(httpRequest)) {
                return ResponseEntity.status(302)
                    .addHeader(LOCATION_HEADER, INDEX_PAGE)
                    .build();
            }
            String loginSession = signUp(httpRequest);
            return ResponseEntity.status(302)
                .addHeader(LOCATION_HEADER, INDEX_PAGE)
                .addHeader("Set-Cookie", "JSESSIONID" + "=" + loginSession)
                .build();
        } catch (MemberAlreadyExistsException e) {
            return ResponseEntity.status(302)
                .addHeader(LOCATION_HEADER, "/register.html")
                .build();
        }
    }

    private String signUp(HttpRequest httpRequest) {
        Map<String, String> bodyData = BodyExtractor.convertBody(httpRequest.getResponseBody());
        String account = bodyData.get(ACCOUNT);
        String password = bodyData.get(PASSWORD);
        String email = bodyData.get(EMAIL);
        return loginService.signUp(account, password, email);
    }

    @Override
    public ResponseEntity<? extends Object> doPut(HttpRequest httpRequest) {
        return ResponseEntity.status(405)
            .build();
    }

    @Override
    public ResponseEntity<? extends Object> doDelete(HttpRequest httpRequest) {
        return ResponseEntity.status(405)
            .build();
    }
}
