package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.http11.HttpProtocol;
import org.apache.coyote.http11.HttpRequestHandler;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.line.Method;
import org.apache.coyote.http11.request.line.Uri;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.util.FileUtils;

public class LoginController implements HttpRequestHandler {

    private static final String LOGIN_FAIL_PAGE = "/401.html";
    private static final String LOGIN_SUCCESS_REDIRECT_URI = "http://localhost:8080/index.html";
    private static final SessionManager sessionManager = SessionManager.getInstance();
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final Method SUPPORTING_METHOD = Method.POST;
    private static final Uri SUPPORTING_URI = new Uri("/login");
    private static final HttpProtocol SUPPORTING_PROTOCOL = HttpProtocol.HTTP_11;

    @Override
    public boolean supports(HttpRequest request) {
        return request.methodEquals(SUPPORTING_METHOD) &&
                request.protocolEquals(SUPPORTING_PROTOCOL) &&
                request.uriEquals(SUPPORTING_URI);
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        String account = request.getFormData(ACCOUNT);
        String password = request.getFormData(PASSWORD);

        Optional<User> found = InMemoryUserRepository.findByAccount(account);
        if (found.isEmpty() || !found.get().checkPassword(password)) {
            return HttpResponse.unauthorized(FileUtils.readFile(LOGIN_FAIL_PAGE), "html");
        }

        UUID jsessionId = UUID.randomUUID();
        Session session = new Session(jsessionId.toString());
        session.setAttributes("user", found.get());
        sessionManager.putSession(jsessionId.toString(), session);

        HttpResponse response = HttpResponse.redirect(LOGIN_SUCCESS_REDIRECT_URI);
        response.setJsessionCookie(jsessionId);
        return response;
    }
}
