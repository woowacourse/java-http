package org.apache.coyote.servlet.servlets;

import java.util.Map;

import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.header.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.service.UserService;

public class RegisterServlet extends AbstractServlet {

    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);

    public RegisterServlet(final SessionManager sessionManager) {
        super(sessionManager);
    }

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        httpResponse.setStatusCode(StatusCode.OK)
            .setBody("/register.html");
    }

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final Map<String, String> bodies = httpRequest.getBodies();
        UserService.save(bodies.get("account"), bodies.get("password"), bodies.get("email"));
        log.info("register success to ID : {}", bodies.get("account"));

        httpResponse.setLocation("302")
            .setLocation("/index.html")
            .setBody("/index.html");
    }
}
