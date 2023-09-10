package org.apache.coyote.http11.controller;

import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.controller.util.BodyExtractor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.service.LoginService;
import org.apache.coyote.http11.session.SessionManager;

public class LoginController extends AbstractController {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String LOCATION_HEADER = "Location";
    private static final String INDEX_PAGE = "/index.html";

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    public ResponseEntity<? extends Object> doGet(HttpRequest httpRequest) {
        if (SessionManager.loggedIn(httpRequest)) {
            return ResponseEntity.redirect(INDEX_PAGE);
        }
        ResponseEntity<Object> responseEntity = ResponseEntity.status(200).build();
        responseEntity.responseView("/login.html");
        return responseEntity;
    }

    @Override
    public ResponseEntity<? extends Object> doPost(HttpRequest httpRequest) {
        if (SessionManager.loggedIn(httpRequest)) {
            return ResponseEntity.redirect(INDEX_PAGE);
        }

        Optional<String> loginSession = login(httpRequest);
        if (loginSession.isPresent()) {
            return ResponseEntity.status(302)
                .addHeader(LOCATION_HEADER, INDEX_PAGE)
                .addHeader("Set-Cookie", "JSESSIONID" + "=" + loginSession.get())
                .build();
        }

        return ResponseEntity.redirect("/401.html");
    }

    private Optional<String> login(HttpRequest httpRequest) {
        Map<String, String> bodyData = BodyExtractor.convertBody(httpRequest.getResponseBody());
        String account = bodyData.get(ACCOUNT);
        String password = bodyData.get(PASSWORD);
        if (account != null && password != null) {
            return loginService.login(account, password);
        }
        return Optional.empty();
    }
}
