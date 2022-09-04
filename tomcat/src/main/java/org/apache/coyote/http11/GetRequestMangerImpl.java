package org.apache.coyote.http11;

import org.apache.catalina.SessionManager;

import java.io.IOException;

public class GetRequestMangerImpl implements RequestManager {

    private static final String PREFIX = "static/";
    private static final Integer STATUS_CODE_OK = 200;
    private static final String OK = "OK";
    private static final String POST_LOGIN_REDIRECT = "static/index.html";

    private final RequestParser requestParser;

    public GetRequestMangerImpl(RequestParser requestParser) {
        this.requestParser = requestParser;
    }

    @Override
    public String generateResponse() throws IOException {
        FileName fileName = requestParser.generateFileName();
        SessionManager sessionManager = new SessionManager();
        HttpCookie httpCookie = requestParser.generateCookie();
        String sessionId = httpCookie.generateJsessionId();
        String responseBody = HtmlLoader.generateFile(PREFIX + fileName.concat());

        if (fileName.getBaseName().equals("/login") && sessionManager.existSession(sessionId)) {
            responseBody = HtmlLoader.generateFile(POST_LOGIN_REDIRECT);
        }

        return String.join("\r\n",
                "HTTP/1.1 " + STATUS_CODE_OK + " " + OK + " ",
                "Content-Type: text/" + fileName.getExtension() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
