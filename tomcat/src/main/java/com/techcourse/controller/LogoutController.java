package com.techcourse.controller;

import com.techcourse.service.UserSessionService;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public final class LogoutController extends AbstractController {

    private final UserSessionService userSessions = new UserSessionService();

    public LogoutController() {
        super(HttpMethod.POST);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        request.getCookie(HttpCookie.JSESSION_ID).ifPresent(userSessions::invalidate);

        response.setStatus(HttpStatus.NO_CONTENT);
    }
}
