package org.apache.coyote.servlet.servlets;

import java.util.Map;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.header.Method;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.service.UserService;

public class RegisterServlet extends Servlet {

    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);

    private static RegisterServlet registerServlet = new RegisterServlet();

    public RegisterServlet() {
    }

    @Override
    public Servlet init() {
        return registerServlet;
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
        return HttpResponse.of(httpRequest, "/register.html", "200");
    }

    private HttpResponse doPost(final HttpRequest httpRequest) {
        final Map<String, String> bodies = httpRequest.getBodies();
        UserService.save(bodies.get("account"), bodies.get("password"), bodies.get("email"));
        log.info("register success to ID : {}", bodies.get("account"));

        return HttpResponse.of(httpRequest, "/index.html", "302");
    }
}
