package org.apache.coyote.http11.http11handler.impl;

import java.util.Map;
import nextstep.jwp.model.visitor.Visitor;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.http11handler.Http11Handler;
import org.apache.coyote.http11.http11handler.login.LoginService;
import org.apache.coyote.http11.http11handler.support.HandlerSupporter;
import org.apache.coyote.http11.http11handler.support.QueryStringProcessor;
import org.apache.coyote.http11.http11request.Http11Request;
import org.apache.coyote.http11.http11response.Http11Response;

public class LoginHandler implements Http11Handler {

    private static final String URI = "/login";
    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";
    private static final HttpMethod HTTP_METHOD = HttpMethod.POST;
    private static final String REDIRECT_WHEN_LOGIN_SUCCESS = "/index.html";
    private static final String REDIRECT_WHEN_LOGIN_FAIL = "/401.html";

    private final LoginService loginService = new LoginService();

    @Override
    public boolean isProperHandler(Http11Request http11Request) {
        return URI.equals(http11Request.getUri()) && http11Request.getHttpMethod().equals(HTTP_METHOD);
    }

    @Override
    public Http11Response handle(Http11Request http11Request, Visitor visitor) {
        Map<String, String> queryStringDatas = QueryStringProcessor.extractQueryStringDatas(http11Request.getBody());
        if (loginService.login(queryStringDatas.get(ACCOUNT_KEY), queryStringDatas.get(PASSWORD_KEY))) {
            visitor.maintainLogin(loginService.findUser(queryStringDatas.get(ACCOUNT_KEY)));
            return HandlerSupporter.resourceResponseComponent(REDIRECT_WHEN_LOGIN_SUCCESS, StatusCode.FOUND);
        }
        return HandlerSupporter.redirectResponseComponent(REDIRECT_WHEN_LOGIN_FAIL, StatusCode.FOUND);
    }
}
