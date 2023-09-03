package org.apache.coyote.handler.dynamichandler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.handler.Handler;
import org.apache.coyote.http11.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class LoginHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);
    private final static String ACCOUNT = "account";
    private final static String PASSWORD = "password";

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        if (HttpMethod.GET == HttpMethod.valueOf(httpRequest.method())) {
            return handleGetMapping(httpRequest);
        }
        return null;
    }

    private HttpResponse handleGetMapping(HttpRequest httpRequest) {
        String quiryString = httpRequest.quiryString();
        if (quiryString != null) {
            String account = parseAccount(quiryString);
            String password = parsePassword(quiryString);
            if (isExistUser(account, password)) {
                log.info(ACCOUNT + ": " + account + ", " + PASSWORD + ": " + password);
                return handleRedirectPage();
            }
            return handleUnAuthorizedPage();
        }
        try {
            Path path = Paths.get(getClass().getClassLoader().getResource("static" + httpRequest.uri() + ".html").getPath());
            byte[] bytes = Files.readAllBytes(path);
            String body = new String(bytes);
            HttpResponse response = new HttpResponse();
            response.setHttpVersion(HttpVersion.HTTP11.value());
            response.setHttpStatus(HttpStatus.OK);
            response.setHeader(HttpHeader.CONTENT_TYPE, ContentType.TEXT_HTML.value())
                    .setHeader(HttpHeader.CONTENT_LENGTH, body.getBytes().length + " ");
            response.setBody(body);
            return response;
        } catch (IOException e) {
        }
        return null;
    }

    private String parseAccount(String quiryString) {
        String[] accountInfo = quiryString.substring(0, quiryString.indexOf('&')).split("=");
        if (ACCOUNT.equals(accountInfo[0])) {
            return accountInfo[1];
        }
        return null;
    }

    private String parsePassword(String quiryString) {
        String[] passwordInfo = quiryString.substring(quiryString.indexOf('&') + 1).split("=");
        if (PASSWORD.equals(passwordInfo[0])) {
            return passwordInfo[1];
        }
        return null;
    }

    private boolean isExistUser(String account, String password) {
        if (account == null || password == null) {
            return false;
        }
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            return true;
        }
        return false;
    }

    private HttpResponse handleUnAuthorizedPage() {
        try {
            Path path = Paths.get(getClass().getClassLoader().getResource("static" + "/" + "401.html").getPath());
            byte[] bytes = Files.readAllBytes(path);
            String body = new String(bytes);
            HttpResponse response = new HttpResponse();
            response.setHttpVersion(HttpVersion.HTTP11.value());
            response.setHttpStatus(HttpStatus.UNAUTHORIZED);
            response.setHeader(HttpHeader.CONTENT_TYPE, ContentType.TEXT_HTML.value())
                    .setHeader(HttpHeader.CONTENT_LENGTH, String.valueOf(body.getBytes().length));
            response.setBody(body);
            return response;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private HttpResponse handleRedirectPage() {
        HttpResponse response = new HttpResponse();
        response.setHttpVersion(HttpVersion.HTTP11.value());
        response.setHttpStatus(HttpStatus.FOUND);
        response.setHeader(HttpHeader.LOCATION, "/index.html");
        return response;
    }
}
