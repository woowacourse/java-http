package org.apache.catalina.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpVersion;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;
import org.apache.coyote.http11.response.StatusLine;

public class RegisterController extends AbstractController {

    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";
    private static final String EMAIL_KEY = "email";

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final String account = request.getBodyValue(ACCOUNT_KEY);
        final String password = request.getBodyValue(PASSWORD_KEY);
        final String email = request.getBodyValue(EMAIL_KEY);
        InMemoryUserRepository.save(new User(account, password, email));

        final HttpResponse registerResponse = getRedirectResponse("/index.html");
        response.copy(registerResponse);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final StatusLine statusLine = new StatusLine(HttpVersion.HTTP_1_1, StatusCode.OK);
        final ContentType contentType = ContentType.HTML;
        final String responseBody = getFileToResponseBody("/register.html");

        final HttpResponse registerResponse = HttpResponse.of(statusLine, contentType, responseBody);
        response.copy(registerResponse);
    }
}
