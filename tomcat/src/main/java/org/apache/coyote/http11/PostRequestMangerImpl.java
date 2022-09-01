package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.UUID;

public class PostRequestMangerImpl implements RequestManager {

    private static final Integer STATUS_CODE = 302;
    private static final String FOUND = "Found";

    private final RequestParser requestParser;

    public PostRequestMangerImpl(RequestParser requestParser) {
        this.requestParser = requestParser;
    }

    @Override
    public String generateResponse() {
        FileName fileName = requestParser.generateFileName();

        String redirect = "";
        System.out.println(fileName.getPrefix() + " " + fileName.getExtension());
        if (fileName.getPrefix().equals("/login")) {
            FormData loginRequestBody = requestParser.generateRequestBody();
            LoginTry loginTry = new LoginTry(loginRequestBody.get("account"), loginRequestBody.get("password"));
            redirect = loginTry.generateRedirectUrl();

            HttpCookie httpCookie = requestParser.generateCookie();
            UUID cookieValue = UUID.randomUUID();
            if (httpCookie.isContains("JSESSIONID")) {
                cookieValue = UUID.fromString(httpCookie.get("JSESSIONID"));
            }
            return String.join("\r\n",
                    "HTTP/1.1 " + STATUS_CODE + " " + FOUND + " ",
                    "Location: " + redirect + " ",
                    "Set-Cookie: JSESSIONID=" + cookieValue + " ",
                    "");
        }

        FormData registerRequestBody = requestParser.generateRequestBody();
        RegisterTry registerTry = new RegisterTry(
                registerRequestBody.get("account"),
                registerRequestBody.get("password"),
                registerRequestBody.get("email"));
        redirect = registerTry.signUp();

        return String.join("\r\n",
                "HTTP/1.1 " + STATUS_CODE + " " + FOUND + " ",
                "Location: " + redirect + " ",
                "");
    }
}
