package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.auth.RegisterService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.line.Protocol;
import org.apache.coyote.http11.response.HttpResponse;

import static org.apache.coyote.http11.response.ResponsePage.REGISTER_PAGE;

public class RegisterController extends AbstractController {

    private final RegisterService registerService = new RegisterService();

    @Override
    protected HttpResponse doPost(HttpRequest request, HttpResponse response) {
        final String account = request.requestBody().getBy("account");
        final String password = request.requestBody().getBy("password");
        final String email = request.requestBody().getBy("email");
        final Protocol protocol = request.getProtocol();
        return registerService.getIndexOrConflictResponse(account, password, email, protocol);
    }

    @Override
    protected HttpResponse doGet(HttpRequest request, HttpResponse response) {
        return HttpResponse.getCookieNullResponseEntity(request.getProtocol(), REGISTER_PAGE);
    }

}
