package org.apache.coyote.http11.controller;

import nextstep.jwp.model.Request;
import nextstep.jwp.vo.FileName;
import nextstep.jwp.vo.HttpCookie;
import nextstep.jwp.vo.Response;
import nextstep.jwp.vo.ResponseStatus;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HtmlLoader;

import java.io.IOException;

import static nextstep.jwp.vo.HttpHeader.*;

public class SessionedLoginController implements Controller {

    private static final String PREFIX = "static/";
    private static final String POST_LOGIN_REDIRECT = "static/index.html";
    private static final SessionedLoginController INSTANCE = new SessionedLoginController();

    @Override
    public Response respond(Request request) throws IOException {
        FileName fileName = request.getFileName();
        SessionManager sessionManager = new SessionManager();
        HttpCookie httpCookie = request.getCookie();
        String sessionId = httpCookie.getJsessionId();
        String responseBody = HtmlLoader.generateFile(PREFIX + fileName.concat());

        if (sessionManager.existSession(sessionId)) {
            responseBody = HtmlLoader.generateFile(POST_LOGIN_REDIRECT);
        }

        return Response.from(ResponseStatus.OK)
                .addHeader(CONTENT_TYPE.getValue(),
                        "text/" + fileName.getExtension() + CHARSET_UTF_8.getValue())
                .addHeader(CONTENT_LENGTH.getValue(), String.valueOf(responseBody.getBytes().length))
                .addBlankLine()
                .addBody(responseBody);
    }

    private SessionedLoginController() {
    }

    public static Controller getInstance() {
        return INSTANCE;
    }
}
