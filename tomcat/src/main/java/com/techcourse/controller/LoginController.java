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

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String requestBody = request.getBody();
        String account = requestBody.split("&")[0].split("=")[1];
        String password = requestBody.split("&")[1].split("=")[1];

        User user = InMemoryUserRepository.findByAccount(account).get();
        String responseBody = new String(request.toHttpResponseBody());

        if (user.checkPassword(password)) {
            log.info("user : {}", user);
            UUID jSessionId = UUID.randomUUID();
            Session session = new Session(jSessionId.toString());
            session.setAttribute("user", user);
            sessionManager.add(session);

            response.addVersion(request.getVersion());
            response.addHttpStatus(HttpStatus.FOUND);
            response.addHeader(HttpHeaders.CONTENT_TYPE, request.getContentType());
            response.addHeader(HttpHeaders.CONTENT_LENGTH, responseBody.getBytes().length);
            response.addHeader(HttpHeaders.LOCATION, "/index.html");
            response.addHeader(HttpHeaders.SET_COOKIE, "JSESSIONID=" + jSessionId);
            response.addBody(responseBody);

        } else {
            response.addVersion(request.getVersion());
            response.addHttpStatus(HttpStatus.FOUND);
            response.addHeader(HttpHeaders.CONTENT_TYPE, request.getContentType());
            response.addHeader(HttpHeaders.CONTENT_LENGTH, responseBody.getBytes().length);
            response.addHeader(HttpHeaders.LOCATION, "/401.html");
            response.addBody(responseBody);
        }
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> headers = request.getHeaders();
        String responseBody = new String(request.toHttpResponseBody());
        if (headers.containsKey(HttpHeaders.COOKIE) &&
                headers.get(HttpHeaders.COOKIE).startsWith("JSESSIONID=")) {
            String jSessionId = headers.get(HttpHeaders.COOKIE).split("=")[1];
            Session session = sessionManager.findSession(jSessionId);

            if (session != null && session.getAttribute("user") != null) {

                response.addVersion(request.getVersion());
                response.addHttpStatus(HttpStatus.FOUND);
                response.addHeader(HttpHeaders.CONTENT_TYPE, request.getContentType());
                response.addHeader(HttpHeaders.CONTENT_LENGTH, responseBody.getBytes().length);
                response.addHeader(HttpHeaders.LOCATION, "/index.html");
                response.addBody(responseBody);
            } else {
                response.addVersion(request.getVersion());
                response.addHttpStatus(HttpStatus.OK);
                response.addHeader(HttpHeaders.CONTENT_TYPE, request.getContentType());
                response.addHeader(HttpHeaders.CONTENT_LENGTH, responseBody.getBytes().length);
                response.addBody(responseBody);
            }
        } else {
            response.addVersion(request.getVersion());
            response.addHttpStatus(HttpStatus.OK);
            response.addHeader(HttpHeaders.CONTENT_TYPE, request.getContentType());
            response.addHeader(HttpHeaders.CONTENT_LENGTH, responseBody.getBytes().length);
            response.addBody(responseBody);
        }
    }
}
