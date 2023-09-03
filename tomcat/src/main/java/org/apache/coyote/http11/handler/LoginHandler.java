package org.apache.coyote.http11.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;
import java.util.UUID;

public class LoginHandler implements HttpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    public boolean support(HttpRequest httpRequest) {
        return httpRequest.isMethodEqualTo("GET") && httpRequest.isUriEqualTo("/login") && httpRequest.hasQueryParameter();
    }

    @Override
    public void handle(HttpRequest httpRequest, OutputStream outputStream) throws IOException {
        final Optional<String> account = httpRequest.getQueryParameter("account");
        final Optional<String> password = httpRequest.getQueryParameter("password");

        if (account.isEmpty() || password.isEmpty()) {
            returnUnauthorizedPage(outputStream);
            return;
        }
        verifyAccount(account.get(), password.get(), outputStream);
    }

    private void verifyAccount(String account, String password, OutputStream outputStream) throws IOException {
        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty() || !user.get().checkPassword(password)) {
            returnUnauthorizedPage(outputStream);
            return;
        }
        logAccount(user.get());
        returnIndexPage(outputStream);
    }

    private void returnUnauthorizedPage(OutputStream outputStream) throws IOException {
        final HttpResponse httpResponse = new HttpResponse.Builder()
                .responseBody(new FileHandler().readFromResourcePath("static/401.html"))
                .responseStatus("401")
                .build(outputStream);
        httpResponse.flush();
    }

    private void returnIndexPage(OutputStream outputStream) throws IOException {
        final HttpResponse httpResponse = new HttpResponse.Builder()
                .responseBody(new FileHandler().readFromResourcePath("static/index.html"))
                .build(outputStream);

        httpResponse.addCookie("JSESSIONID", UUID.randomUUID().toString());

        httpResponse.flush();
    }

    private void logAccount(User user) {
        log.info("user : {}", user);
    }
}
