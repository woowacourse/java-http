package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.HttpHeaderNames;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.UUID;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final SessionManager sessionManager = new SessionManager();
    private static final String JSESSIONID = "JSESSIONID";

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String account = request.findRequestBodyValue("account");
        String password = request.findRequestBodyValue("password");

        User user = InMemoryUserRepository.findByAccount(account).get();

        if (user.checkPassword(password)) {
            log.info("user : {}", user);
            UUID jSessionId = saveUUID(user);

            response.setResponseFromRequest(request);
            response.addHttpStatus(HttpStatus.FOUND);
            response.addHeader(HttpHeaderNames.LOCATION, "/index.html");
            response.addHeader(HttpHeaderNames.SET_COOKIE, JSESSIONID + "=" + jSessionId);

        } else {
            response.setResponseFromRequest(request);
            response.addHttpStatus(HttpStatus.FOUND);
            response.addHeader(HttpHeaderNames.LOCATION, "/401.html");
        }
    }

    private UUID saveUUID(User user) {
        UUID jSessionId = UUID.randomUUID();
        Session session = new Session(jSessionId.toString());
        session.setAttribute("user", user);
        sessionManager.add(session);
        return jSessionId;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        HttpHeaders headers = request.getHttpHeaders();

        if (headers.isCookieExistBy(JSESSIONID)) {
            String jSessionId = headers.getCookieBy(JSESSIONID);
            Session session = sessionManager.findSession(jSessionId);

            if (session != null && session.getAttribute("user") != null) {
                response.setResponseFromRequest(request);
                response.addHttpStatus(HttpStatus.FOUND);
                response.addHeader(HttpHeaderNames.LOCATION, "/index.html");
            } else {
                response.setResponseFromRequest(request);
                response.addHttpStatus(HttpStatus.OK);
            }
        } else {
            response.setResponseFromRequest(request);
            response.addHttpStatus(HttpStatus.OK);
        }
    }
}
