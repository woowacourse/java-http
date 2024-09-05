package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.HttpRequestHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.line.HttpProtocol;
import org.apache.coyote.http11.request.line.Method;
import org.apache.coyote.http11.request.line.Uri;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController implements HttpRequestHandler {

    private static final String ACCOUNT_FORM_DATA = "account";
    private static final String PASSWORD_FORM_DATA = "password";
    private static final String EMAIL_FORM_DATA = "email";
    private static final String REGISTER_SUCCESS_REDIRECT_URI = "http://localhost:8080/index.html";
    private static final Method SUPPORTING_METHOD = Method.POST;
    private static final Uri SUPPORTING_URI = new Uri("/register");
    private static final HttpProtocol SUPPORTING_PROTOCOL = HttpProtocol.HTTP_11;
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final AtomicLong userIdGenerator = new AtomicLong(2L);

    @Override
    public boolean supports(final HttpRequest request) {
        if (request.isMethodNotEqualWith(SUPPORTING_METHOD)) {
            return false;
        }
        if (request.isHttpProtocolNotEqualWith(SUPPORTING_PROTOCOL)) {
            return false;
        }
        if (request.isUriNotStartsWith(SUPPORTING_URI)) {
            return false;
        }
        return true;
    }

    @Override
    public HttpResponse handle(final HttpRequest request) throws IOException {
        String account = request.getFormData(ACCOUNT_FORM_DATA);
        String password = request.getFormData(PASSWORD_FORM_DATA);
        String email = request.getFormData(EMAIL_FORM_DATA);

        User user = new User(userIdGenerator.getAndIncrement(), account, password, email);
        InMemoryUserRepository.save(user);
        return HttpResponse.redirect(REGISTER_SUCCESS_REDIRECT_URI);
    }
}
