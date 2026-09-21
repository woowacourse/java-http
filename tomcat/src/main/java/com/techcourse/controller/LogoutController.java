package com.techcourse.controller;

import com.techcourse.service.UserSessionService;
import org.apache.catalina.AbstractController;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public final class LogoutController extends AbstractController {

    private final UserSessionService userSessions = new UserSessionService();

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String sessionId = request.getCookie(HttpCookie.JSESSION_ID).orElseThrow();
        userSessions.invalidate(sessionId);

        response.setStatus(HttpStatus.NO_CONTENT);
    }
}
