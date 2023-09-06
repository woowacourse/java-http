package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.ResponseEntity;

public class RegisterHandler extends UserHandler {

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.getRequestLine();

        return "/register".equals(requestLine.getPath());
    }

    @Override
    protected ResponseEntity doGet(RequestLine requestLine) {
        if (requestLine.isQueryStringExisted()) {
            String account = requestLine.findQueryStringValue("account");
            String password = requestLine.findQueryStringValue("password");
            String email = requestLine.findQueryStringValue("email");

            return userController.signUp(account, password, email);
        }
        return new ResponseEntity(HttpStatus.OK, requestLine.getPath());
    }

    @Override
    protected ResponseEntity doPost(RequestBody requestBody) {
        String account = requestBody.get("account");
        String password = requestBody.get("password");
        String email = requestBody.get("email");

        return userController.signUp(account, password, email);
    }
}
