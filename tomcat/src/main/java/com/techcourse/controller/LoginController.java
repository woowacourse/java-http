package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.HttpStatus;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.UUID;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final SessionManager sessionManager = new SessionManager();
    private static final String JSESSIONID = "JSESSIONID=";

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String requestBody = request.getBody();
        String account = requestBody.split("&")[0].split("=")[1];
        String password = requestBody.split("&")[1].split("=")[1];

        User user = InMemoryUserRepository.findByAccount(account).get();

        if (user.checkPassword(password)) {
            log.info("user : {}", user);
            UUID jSessionId = UUID.randomUUID();
            Session session = new Session(jSessionId.toString());
            session.setAttribute("user", user);
            sessionManager.add(session);

            response.setResponseFromRequest(request);
            response.addHttpStatus(HttpStatus.FOUND);
            response.addHeader(HttpHeaders.LOCATION, "/index.html");
            response.addHeader(HttpHeaders.SET_COOKIE, JSESSIONID + jSessionId);

        } else {
            response.setResponseFromRequest(request);
            response.addHttpStatus(HttpStatus.FOUND);
            response.addHeader(HttpHeaders.LOCATION, "/401.html");
        }
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> headers = request.getHeaders();
        if (headers.containsKey(HttpHeaders.COOKIE) &&
                headers.get(HttpHeaders.COOKIE).startsWith(JSESSIONID)) {
            String jSessionId = headers.get(HttpHeaders.COOKIE).split("=")[1];
            Session session = sessionManager.findSession(jSessionId);

            if (session != null && session.getAttribute("user") != null) {
                response.setResponseFromRequest(request);
                response.addHttpStatus(HttpStatus.FOUND);
                response.addHeader(HttpHeaders.LOCATION, "/index.html");
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
