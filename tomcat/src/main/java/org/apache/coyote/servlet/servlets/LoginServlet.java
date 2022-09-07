package org.apache.coyote.servlet.servlets;

import java.util.Map;
import java.util.Optional;

import org.apache.coyote.http11.SessionFactory;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.header.Method;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;

public class LoginServlet extends AbstractServlet {

    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);

    public LoginServlet(final SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        final Method method = httpRequest.getMethod();

        if (method.isGet()) {
            return doGet(httpRequest);
        }
        if (method.isPost()) {
            return doPost(httpRequest);
        }
        return createNotFoundResponse(httpRequest);
    }

    private HttpResponse doGet(final HttpRequest httpRequest) {
        if (sessionFactory.isLoginAccount(httpRequest)) {
            return HttpResponse.of(httpRequest, "/index.html", "302");
        }
        return HttpResponse.of(httpRequest, "/login.html", "200");
    }

    private HttpResponse doPost(final HttpRequest httpRequest) {
        final Map<String, String> bodies = httpRequest.getBodies();
        if (isNotContainEssentialParams(bodies)) {
            return HttpResponse.of(httpRequest, "/404.html", "404");
        }
        final Optional<User> user = UserService.find(bodies.get("account"), bodies.get("password"));

        if (user.isPresent()) {
            log.info("login success to ID : {}", user.get().getAccount());
            final HttpResponse httpResponse = HttpResponse.cookie(httpRequest, "/index.html", "302");

            sessionFactory.add(user.get(), httpResponse);

            return httpResponse;
        }
        return HttpResponse.of(httpRequest, "/401.html", "401");
    }

    private boolean isNotContainEssentialParams(final Map<String, String> bodies) {
        return !bodies.containsKey("account") || !bodies.containsKey("password");
    }
}
