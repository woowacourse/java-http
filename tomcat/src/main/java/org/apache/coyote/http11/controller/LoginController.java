package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.auth.LoginService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.line.HttpMethod;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends AbstractController {

    private final LoginService loginService = new LoginService();

    @Override
    public HttpResponse service(HttpRequest request, HttpResponse response) {
        if (request.methodIsEqualTo(HttpMethod.GET)) {
            return doGet(request, response);
        }
        return doPost(request, response);
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request, final HttpResponse response) {
        RequestBody requestBody = request.requestBody();
        final String account = requestBody.getBy("account");
        final String password = requestBody.getBy("password");
        return loginService.getLoginOrElseUnAuthorizedResponse(
                request.getProtocol(),
                account,
                password
        );
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request, final HttpResponse response) {
        return loginService.getLoginViewResponse(request, response);
    }

}
