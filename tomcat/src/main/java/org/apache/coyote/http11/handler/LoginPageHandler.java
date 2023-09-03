package org.apache.coyote.http11.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import org.apache.coyote.http11.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

public class LoginPageHandler implements HttpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    public boolean support(HttpRequest httpRequest) {
        return httpRequest.isMethodEqualTo("GET") && httpRequest.isUriEqualTo("/login");
    }

    @Override
    public void handle(HttpRequest httpRequest, OutputStream outputStream) throws IOException {
        final String account = httpRequest.getQueryParameter("account");
        final String password = httpRequest.getQueryParameter("password");

        verifyAccount(account, password);

        final HttpResponse httpResponse = new HttpResponse.Builder()
                .responseBody(new FileHandler().readFromResourcePath("static/login.html"))
                .build(outputStream);
        httpResponse.flush();
    }

    private static void verifyAccount(String account, String password) {
        InMemoryUserRepository.findByAccount(account)
                .ifPresent(user -> {
                    if (user.checkPassword(password)) {
                        log.info("user : {}", user);
                    }
                });
    }
}
