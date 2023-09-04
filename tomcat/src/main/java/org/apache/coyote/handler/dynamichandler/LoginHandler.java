package org.apache.coyote.handler.dynamichandler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.handler.Handler;
import org.apache.coyote.handler.session.Session;
import org.apache.coyote.handler.session.SessionManager;
import org.apache.coyote.handler.statichandler.ExceptionHandler;
import org.apache.coyote.http11.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

public class LoginHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);
    private final static String ACCOUNT = "account";
    private final static String PASSWORD = "password";

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        if (httpRequest.session() != null && !"".equals(httpRequest.session())) {
            return handleAlreadyLogin();
        }
        if (HttpMethod.GET == HttpMethod.valueOf(httpRequest.method())) {
            return handleGetMapping(httpRequest);
        }
        if (HttpMethod.POST == HttpMethod.valueOf(httpRequest.method())) {
            return handlePostMapping(httpRequest);
        }
        return null;
    }

    private HttpResponse handleGetMapping(HttpRequest httpRequest) {
        String quiryString = httpRequest.quiryString();
        if (quiryString != null) {
            String account = parseAccount(quiryString);
            String password = parsePassword(quiryString);
            Optional<User> user = InMemoryUserRepository.findByAccount(account);
            if (user.isPresent() && user.get().checkPassword(password)) {
                log.info(ACCOUNT + ": " + account + ", " + PASSWORD + ": " + password);
                return handleRedirectPage(user.get());
            }
            return handleUnAuthorizedPage(httpRequest);
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

    private HttpResponse handlePostMapping(HttpRequest httpRequest) {
        String body = httpRequest.body();
        if (body != null) {
            String account = parseAccount(body);
            String password = parsePassword(body);
            Optional<User> user = InMemoryUserRepository.findByAccount(account);
            if (user.isPresent() && user.get().checkPassword(password)) {
                log.info(ACCOUNT + ": " + account + ", " + PASSWORD + ": " + password);
                return handleRedirectPage(user.get());
            }
            return handleUnAuthorizedPage(httpRequest);
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

    private HttpResponse handleUnAuthorizedPage(HttpRequest httpRequest) {
        return new ExceptionHandler(HttpStatus.UNAUTHORIZED).handle(httpRequest);
    }

    private HttpResponse handleRedirectPage(User user) {
        UUID uuid = UUID.randomUUID();
        Session session = new Session(uuid.toString());
        session.setAttribute("user", user);
        new SessionManager().add(session);
        HttpResponse response = new HttpResponse();
        response.setHttpVersion(HttpVersion.HTTP11.value());
        response.setHttpStatus(HttpStatus.FOUND);
        response.setHeader(HttpHeader.LOCATION, "/index.html")
                .setHeader(HttpHeader.SET_COOKIE, "JSESSIONID=" + uuid);
        return response;
    }

    private HttpResponse handleAlreadyLogin() {
        HttpResponse response = new HttpResponse();
        response.setHttpVersion(HttpVersion.HTTP11.value());
        response.setHttpStatus(HttpStatus.FOUND);
        response.setHeader(HttpHeader.LOCATION, "/index.html");
        return response;
    }
}
