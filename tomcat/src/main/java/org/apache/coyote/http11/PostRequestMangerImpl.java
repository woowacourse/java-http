package org.apache.coyote.http11;

import org.apache.catalina.SessionManager;

public class PostRequestMangerImpl implements RequestManager {

    private static final Integer STATUS_CODE_FOUND = 302;
    private static final String FOUND = "Found";

    private final RequestParser requestParser;

    public PostRequestMangerImpl(RequestParser requestParser) {
        this.requestParser = requestParser;
    }

    @Override
    public String generateResponse() {
        FileName fileName = requestParser.generateFileName();
        FormData requestBody = requestParser.generateRequestBody();
        SessionManager sessionManager = new SessionManager();

        if (fileName.getBaseName().equals("/login")) {
            LoginService loginService = new LoginService();
            LoginResult loginResult = loginService.signIn(requestBody.get("account"), requestBody.get("password"));

            return String.join("\r\n",
                    "HTTP/1.1 " + STATUS_CODE_FOUND + " " + FOUND + " ",
                    "Set-Cookie: JSESSIONID=" + loginResult.getSession().getId() + " ",
                    "Location: " + loginResult.getRedirectUrl() + " ",
                    "");
        }

        RegisterService registerService = new RegisterService();
        String redirect = registerService.signUp(
                requestBody.get("account"),
                requestBody.get("password"),
                requestBody.get("email")
        );

        return String.join("\r\n",
                "HTTP/1.1 " + STATUS_CODE_FOUND + " " + FOUND + " ",
                "Location: " + redirect + " ",
                "");
    }
}
