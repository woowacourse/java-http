package org.apache.coyote.servlet.servlets;

import java.util.Map;

import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.header.Method;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.service.UserService;

public class RegisterServlet extends AbstractServlet {

    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);

    public RegisterServlet(final SessionManager sessionManager) {
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
        httpResponse.setStatusCode("200")
            .setBody("/register.html");
    }

    private void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final Map<String, String> bodies = httpRequest.getBodies();
        UserService.save(bodies.get("account"), bodies.get("password"), bodies.get("email"));
        log.info("register success to ID : {}", bodies.get("account"));

        httpResponse.setLocation("302")
            .setLocation("/index.html")
            .setBody("/index.html");
    }
}
