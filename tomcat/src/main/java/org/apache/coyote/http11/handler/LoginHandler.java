package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.ResponseEntity;

public class LoginHandler extends UserHandler {

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.getRequestLine();

        return "/login".equals(requestLine.getPath());
    }

    @Override
    protected ResponseEntity doGet(HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.getRequestLine();
        if (requestLine.isQueryStringExisted()) {
            String account = requestLine.findQueryStringValue("account");
            String password = requestLine.findQueryStringValue("password");

            return login(account, password);
        }

        return ResponseEntity.of(HttpStatus.OK, requestLine.getPath());
    }

    @Override
    protected ResponseEntity doPost(HttpRequest httpRequest) {
        RequestBody requestBody = httpRequest.getRequestBody();
        String account = requestBody.get("account");
        String password = requestBody.get("password");

        return login(account, password);
    }

    private ResponseEntity login(String account, String password) {
        try {

            return userController.login(account, password);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.of(HttpStatus.FOUND, "/401");
        }
    }
}
