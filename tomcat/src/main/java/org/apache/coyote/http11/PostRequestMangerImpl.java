package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;

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
        }

        return String.join("\r\n",
                "HTTP/1.1 " + STATUS_CODE + " " + FOUND + " ",
                "Location: " + redirect + " ",
                "");
    }
}
