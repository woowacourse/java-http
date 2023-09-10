package org.apache.coyote.handler.dynamichandler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.handler.statichandler.ExceptionHandler;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.Query;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

public class LoginHandler extends AbstractHandler {

    private static final String DEFAULT_DIRECTORY_PATH = "static";
    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            /*
                    httpRequest.session() returns null when Request does not contain Session Information.
                                          throws IllegalArgumentException when Request contains Session Information, but session is invalid.

                    HttpResponse.createStaticResponseByPath throws IOException when File is not Found because FilePath is not invalid.
             */
            Session session = httpRequest.session();
            if (session == null) {
                handleNotLogin(httpRequest, httpResponse);
                return;
            }
            handleAlreadyLogin(httpRequest, httpResponse, session);
        } catch (IllegalArgumentException e) {
            handleNotLogin(httpRequest, httpResponse);
        }
    }

    private void handleNotLogin(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            String body = findStaticPage(DEFAULT_DIRECTORY_PATH + httpRequest.path() + ContentType.TEXT_HTML.extension());

            httpResponse.setStatus(HttpStatus.OK);
            httpResponse.setHeader(HttpHeader.CONTENT_TYPE.value(), ContentType.TEXT_HTML.value());
            httpResponse.setHeader(HttpHeader.CONTENT_LENGTH.value(), String.valueOf(body.getBytes().length));
            httpResponse.setBody(body);
        } catch (IOException e) {
            new ExceptionHandler(HttpStatus.NOT_FOUND).service(httpRequest, httpResponse);
        }
    }

    private String findStaticPage(String path) throws IOException {
        File file = new File(HttpResponse.class
                .getClassLoader()
                .getResource(path)
                .getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }

    private void handleAlreadyLogin(HttpRequest httpRequest, HttpResponse httpResponse, Session session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            session.invalidate();
            httpResponse.setHeader(HttpHeader.SET_COOKIE.value(), Session.JSESSIONID + "=; " + "Max-Age=0 ");
        }

        httpResponse.setStatus(HttpStatus.FOUND);
        httpResponse.setHeader(HttpHeader.LOCATION.value(), "/index.html");
    }

    @Override
    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        Query queries = Query.create(httpRequest.body());

        String account = queries.get(ACCOUNT);
        String password = queries.get(PASSWORD);
        if (account == null || password == null) {
            httpResponse.setStatus(HttpStatus.FOUND);
            httpResponse.setHeader(HttpHeader.LOCATION.value(), "/login.html");
            return;
        }
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            log.info(ACCOUNT + ": " + account + ", " + PASSWORD + ": " + password);
            handleLoginSuccess(httpRequest, httpResponse, user.get());
            return;
        }
        handleUnauthorizedPage(httpRequest, httpResponse);
    }

    private void handleLoginSuccess(HttpRequest httpRequest, HttpResponse httpResponse, User user) {
        Session session = Session.create(UUID.randomUUID().toString());
        session.setAttribute("user", user);

        httpResponse.setStatus(HttpStatus.FOUND);
        httpResponse.setHeader(HttpHeader.LOCATION.value(), "/index.html");
        httpResponse.setHeader(HttpHeader.SET_COOKIE.value(), Session.JSESSIONID + "=" + session.getId());
    }

    private void handleUnauthorizedPage(HttpRequest httpRequest, HttpResponse httpResponse) {
        new ExceptionHandler(HttpStatus.UNAUTHORIZED).service(httpRequest, httpResponse);
    }
}
