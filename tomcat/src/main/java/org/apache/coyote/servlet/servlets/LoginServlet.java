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
        throw new IllegalArgumentException(
            String.format("정의되지 않은 method 입니다. [%s, %s]", httpRequest.getUrl(), method)
        );
    }

    private HttpResponse doGet(final HttpRequest httpRequest) {
        final Map<String, String> queries = httpRequest.getQueries();
        if (!queries.containsKey("account")) {
            return HttpResponse.of(httpRequest.getHttpVersion(), "/login.html", "200");
        }
        final Optional<User> user = UserService.findUser(queries.get("account"), queries.get("password"));
        log.info("user : {}", user);

        if (user.isPresent()) {
            return HttpResponse.of(httpRequest.getHttpVersion(), "/index.html", "302");
        }
        return HttpResponse.of(httpRequest.getHttpVersion(), "/401.html", "401");
    }
}
