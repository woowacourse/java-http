package org.apache.coyote.http11;

import nextstep.jwp.model.Request;
import nextstep.jwp.vo.FileName;
import nextstep.jwp.vo.HttpCookie;
import nextstep.jwp.vo.Response;
import nextstep.jwp.vo.ResponseStatus;
import org.apache.catalina.SessionManager;

import java.io.IOException;

public class GetRequestMangerImpl implements RequestManager {

    private static final String PREFIX = "static/";
    private static final String POST_LOGIN_REDIRECT = "static/index.html";
    private static final String LOGIN = "/login";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CHARSET_UTF_8 = ";charset=utf-8";

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
        System.out.println(fileName.getBaseName());

        if (fileName.isSame(LOGIN) && sessionManager.existSession(sessionId)) {
            responseBody = HtmlLoader.generateFile(POST_LOGIN_REDIRECT);
        }

        return Response.from(ResponseStatus.OK)
                .addHeader(CONTENT_TYPE, "text/" + fileName.getExtension() + CHARSET_UTF_8)
                .addHeader(CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length))
                .addBlankLine()
                .addBody(responseBody)
                .getResponse();
    }
}
