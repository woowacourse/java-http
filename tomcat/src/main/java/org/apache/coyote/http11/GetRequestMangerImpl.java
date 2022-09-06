package org.apache.coyote.http11;

import nextstep.jwp.model.Request;
import nextstep.jwp.vo.FileName;
import nextstep.jwp.vo.HttpCookie;
import nextstep.jwp.vo.Response;
import nextstep.jwp.vo.ResponseStatus;
import org.apache.catalina.SessionManager;

import java.io.IOException;

import static nextstep.jwp.vo.HttpHeader.*;

public class GetRequestMangerImpl implements RequestManager {

    private static final String PREFIX = "static/";
    private static final String POST_LOGIN_REDIRECT = "static/index.html";
    private static final String LOGIN = "/login";

    private final Request request;

    public GetRequestMangerImpl(Request request) {
        this.request = request;
    }

    @Override
    public String generateResponse() throws IOException {
        FileName fileName = request.getFileName();
        SessionManager sessionManager = new SessionManager();
        HttpCookie httpCookie = request.getCookie();
        String sessionId = httpCookie.getJsessionId();
        String responseBody = HtmlLoader.generateFile(PREFIX + fileName.concat());

        if (fileName.isSame(LOGIN) && sessionManager.existSession(sessionId)) {
            responseBody = HtmlLoader.generateFile(POST_LOGIN_REDIRECT);
        }

        return Response.from(ResponseStatus.OK)
                .addHeader(CONTENT_TYPE.getValue(),
                        "text/" + fileName.getExtension() + CHARSET_UTF_8.getValue())
                .addHeader(CONTENT_LENGTH.getValue(), String.valueOf(responseBody.getBytes().length))
                .addBlankLine()
                .addBody(responseBody)
                .getResponse();
    }
}
