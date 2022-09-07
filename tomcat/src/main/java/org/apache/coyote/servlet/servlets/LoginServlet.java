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
        return createNotFoundResponse(httpRequest);
    }

    private HttpResponse doGet(final HttpRequest httpRequest) {
        return HttpResponse.of(httpRequest, "/login.html", "200");
    }

    private HttpResponse doPost(final HttpRequest httpRequest) {
        final Map<String, String> bodies = httpRequest.getBodies();
        if (!bodies.containsKey("account") || !bodies.containsKey("password")) {
            return HttpResponse.of(httpRequest, "/404.html", "404");
        }
        final Optional<User> user = UserService.find(bodies.get("account"), bodies.get("password"));

        if (user.isPresent()) {
            log.info("login success to ID : {}", user.get().getAccount());
            return HttpResponse.cookie(
                httpRequest, "/index.html", "302", "JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46"
            );
        }
        return HttpResponse.of(httpRequest, "/401.html", "401");
    }
}
