package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Optional;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.HttpProtocol;
import org.apache.coyote.http11.HttpRequestHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.line.Method;
import org.apache.coyote.http11.request.line.Uri;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements HttpRequestHandler {

    private static final String LOGIN_FAIL_PAGE = "/401.html";
    private static final String LOGIN_SUCCESS_REDIRECT_URI = "http://localhost:8080/index.html";
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String ACCOUNT_PARAMETER = "account";
    private static final String PASSWORD_PARAMETER = "password";
    private static final Method SUPPORTING_METHOD = Method.GET;
    private static final Uri SUPPORTING_URI = new Uri("/login");
    private static final HttpProtocol SUPPORTING_PROTOCOL = HttpProtocol.HTTP_11;

    @Override
    public boolean supports(final HttpRequest request) {
        if (request.methodNotEqual(SUPPORTING_METHOD)) {
            return false;
        }
        if (request.protocolNotEqual(SUPPORTING_PROTOCOL)) {
            return false;
        }
        if (request.uriNotStartsWith(SUPPORTING_URI)) {
            return false;
        }
        if (request.getQueryParameter(ACCOUNT_PARAMETER) == null) {
            return false;
        }
        if (request.getQueryParameter(PASSWORD_PARAMETER) == null) {
            return false;
        }
        return true;
    }

    @Override
    public HttpResponse handle(final HttpRequest request) throws IOException {
        String account = request.getQueryParameter(ACCOUNT_PARAMETER);
        String password = request.getQueryParameter(PASSWORD_PARAMETER);

        Optional<User> found = InMemoryUserRepository.findByAccount(account);
        if (found.isEmpty() || !found.get().checkPassword(password)) {
            return HttpResponse.ok(FileUtils.readFile(LOGIN_FAIL_PAGE), "html");
        }
        HttpResponse response = HttpResponse.redirect(LOGIN_SUCCESS_REDIRECT_URI);
        response.setJsessionCookie();
        return response;
    }
}
