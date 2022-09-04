package org.apache.coyote.http11;

import nextstep.jwp.service.LoginService;
import nextstep.jwp.service.RegisterService;
import nextstep.jwp.vo.FileName;
import nextstep.jwp.vo.FormData;
import nextstep.jwp.vo.LoginResult;

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

        if (fileName.getBaseName().equals("/login")) {
            LoginResult loginResult = LoginService.signIn(requestBody.get("account"), requestBody.get("password"));

            return String.join("\r\n",
                    "HTTP/1.1 " + STATUS_CODE_FOUND + " " + FOUND + " ",
                    "Set-Cookie: JSESSIONID=" + loginResult.getSession().getId() + " ",
                    "Location: " + loginResult.getRedirectUrl() + " ",
                    "");
        }

        String redirect = RegisterService.signUp(
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
