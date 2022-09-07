package org.apache.coyote.servlet.servlets;

import java.util.Map;
import java.util.Optional;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.header.Method;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;

public class LoginServlet extends Servlet {

    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);

    private static LoginServlet loginServlet = new LoginServlet();

    public LoginServlet() {
    }

    @Override
    public Servlet init() {
        return loginServlet;
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
        return throwIfMethodDoesNotDefine(httpRequest, method);
    }

    private HttpResponse doGet(final HttpRequest httpRequest) {
        return HttpResponse.of(httpRequest.getHttpVersion(), "/login.html", "200");
    }

    private HttpResponse doPost(final HttpRequest httpRequest) {
        final Map<String, String> bodies = httpRequest.getBodies();
        if (!bodies.containsKey("account") || !bodies.containsKey("password")) {
            return HttpResponse.of(httpRequest.getHttpVersion(), "/404.html", "404");
        }
        final Optional<User> user = UserService.findUser(bodies.get("account"), bodies.get("password"));

        if (user.isPresent()) {
            log.info("login success to ID : {}", user.get().getAccount());
            return HttpResponse.of(httpRequest.getHttpVersion(), "/index.html", "302");
        }
        return HttpResponse.of(httpRequest.getHttpVersion(), "/401.html", "401");
    }
}
