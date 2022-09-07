package org.apache.coyote.http11.http11handler.impl;

import java.util.Map;
import nextstep.jwp.model.user.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.cookie.HttpCookie;
import org.apache.coyote.http11.http11handler.Http11Handler;
import org.apache.coyote.http11.http11handler.login.LoginService;
import org.apache.coyote.http11.http11handler.support.HandlerSupporter;
import org.apache.coyote.http11.http11handler.support.QueryStringProcessor;
import org.apache.coyote.http11.http11request.Http11Request;
import org.apache.coyote.http11.http11response.ResponseComponent;

public class LoginHandler implements Http11Handler {

    private static final String URI = "/login";
    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";
    private static final HttpMethod HTTP_METHOD = HttpMethod.POST;
    private static final String REDIRECT_WHEN_LOGIN_SUCCESS = "/index.html";
    private static final String REDIRECT_WHEN_LOGIN_FAIL = "/401.html";

    private final HandlerSupporter handlerSupporter = new HandlerSupporter();
    private final QueryStringProcessor queryStringProcessor = new QueryStringProcessor();
    private final LoginService loginService = new LoginService();
    private final SessionManager sessionManager = SessionManager.of();

    @Override
    public boolean isProperHandler(Http11Request http11Request) {
        return URI.equals(http11Request.getUri()) && http11Request.getHttpMethod().equals(HTTP_METHOD);
    }

    @Override
    public ResponseComponent handle(Http11Request http11Request) {
        Map<String, String> queryStringDatas = queryStringProcessor.extractQueryStringDatas(http11Request.getBody());
        if (loginService.login(queryStringDatas.get(ACCOUNT_KEY), queryStringDatas.get(PASSWORD_KEY))) {
            return loginSuccessResponseComponent(REDIRECT_WHEN_LOGIN_SUCCESS, StatusCode.REDIRECT, queryStringDatas.get(ACCOUNT_KEY));
        }
        return handlerSupporter.redirectResponseComponent(REDIRECT_WHEN_LOGIN_FAIL, StatusCode.REDIRECT);
    }

    private ResponseComponent loginSuccessResponseComponent(String uri, StatusCode statusCode, String account) {
        ResponseComponent responseComponent = handlerSupporter.resourceResponseComponent(uri, statusCode);
        User user = loginService.findUser(account);
        Session session = sessionManager.generateSession(user);
        sessionManager.add(session);

        HttpCookie httpCookie = new HttpCookie();
        httpCookie.setJsessionId(session);
        responseComponent.setCookie(httpCookie.toString());
        return responseComponent;
    }
}
