package org.apache.coyote.servlet.servlets;

import java.util.Map;
import java.util.Optional;

import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.header.Method;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;

public class LoginServlet extends AbstractServlet {

    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);

    public LoginServlet(final SessionManager sessionManager) {
        super(sessionManager);
    }

    @Override
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final Method method = httpRequest.getMethod();

        if (method.isGet()) {
            doGet(httpRequest, httpResponse);
            return;
        }
        if (method.isPost()) {
            doPost(httpRequest, httpResponse);
            return;
        }
        setNotFound(httpResponse);
    }

    private void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        if (sessionManager.isLoginAccount(httpRequest)) {
            httpResponse.setStatusCode("302")
                .setLocation("/index.html");
            return;
        }
        httpResponse.setStatusCode("200")
            .setBody("/login.html");
    }

    private void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final Map<String, String> bodies = httpRequest.getBodies();
        if (isNotContainEssentialParams(bodies)) {
            setNotFound(httpResponse);
            return;
        }
        final Optional<User> user = UserService.find(bodies.get("account"), bodies.get("password"));
        if (user.isEmpty()) {
            setUnauthorized(httpResponse);
            return;
        }

        log.info("login success to ID : {}", user.get().getAccount());
        httpResponse.setStatusCode("302")
            .setLocation("/index.html")
            .generateSessionId();
        sessionManager.add(user.get(), httpResponse);
    }

    private boolean isNotContainEssentialParams(final Map<String, String> bodies) {
        return !bodies.containsKey("account") || !bodies.containsKey("password");
    }
}
