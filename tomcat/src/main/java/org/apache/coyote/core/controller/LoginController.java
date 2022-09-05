package org.apache.coyote.core.controller;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.reqeust.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.io.ClassPathResource;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.manager.Session;
import org.apache.coyote.manager.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    public void service(final HttpRequest request, final HttpResponse response)
            throws IOException, UncheckedServletException {
        String method = request.getMethod();
        if (method.equals("POST")) {
            doPost(request, response);
        }
        if (method.equals("GET")) {
            doGet(request, response);
        }
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response)
            throws IOException, UncheckedServletException {
        String responseBody = new ClassPathResource().getStaticContent(request.getPath());

        response.setStatus("OK");
        response.setContentType(request.findContentType());
        response.setContentLength(responseBody.getBytes().length);
        response.setResponseBody(responseBody);

        Login(request, response);
    }

    private void Login(final HttpRequest request, final HttpResponse response) {
        Map<String, String> requestBodies = request.getRequestBodies();
        String account = requestBodies.get("account");
        String password = requestBodies.get("password");

        User user = findUser(account);

        response.setStatus("FOUND");
        response.setLocation("./401.html");

        if (user.checkPassword(password)) {
            response.setStatus("FOUND");
            response.setLocation("./index.html");

            HttpCookie cookie = new HttpCookie();
            response.setCookie(cookie);
            Session session = new Session("user", user);
            SessionManager.add(cookie.getCookie(), session);
        }
    }

    private User findUser(final String account) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(UserNotFoundException::new);
        printUser(user);
        return user;
    }

    private void printUser(final User user) {
        log.info(user.toString());
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response)
            throws IOException, UncheckedServletException {
        if (SessionManager.hasSession(request.getCookie())) {
            response.setLocation("./index.html");
            response.setStatus("FOUND");
            return;
        }

        String responseBody = new ClassPathResource().getStaticContent(request.getPath());

        response.setStatus("OK");
        response.setContentType(request.findContentType());
        response.setContentLength(responseBody.getBytes().length);
        response.setResponseBody(responseBody);
    }
}
