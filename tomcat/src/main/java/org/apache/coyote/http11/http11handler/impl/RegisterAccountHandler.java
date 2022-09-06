package org.apache.coyote.http11.http11handler.impl;

import java.util.Map;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.dto.ResponseComponent;
import org.apache.coyote.http11.http11handler.Http11Handler;
import org.apache.coyote.http11.http11handler.support.HandlerSupporter;
import org.apache.coyote.http11.http11handler.support.QueryStringProcessor;
import org.apache.coyote.http11.http11handler.user.UserService;
import org.apache.coyote.http11.http11request.Http11Request;

public class RegisterAccountHandler implements Http11Handler {

    private static final String URI = "/register";
    private static final HttpMethod ALLOWED_HTTP_METHOD = HttpMethod.POST;
    private static final String ACCOUNT_KEY = "account";
    private static final String EMAIL_KEY = "email";
    private static final String PASSWORD_KEY = "password";
    private static final String REDIRECT_WHEN_REGISTER_SUCCESS = "/index.html";
    private static final String REDIRECT_WHEN_REGISTER_FAIL = "/500.html";


    private final UserService userService = new UserService();
    private final HandlerSupporter handlerSupporter = new HandlerSupporter();
    private final QueryStringProcessor queryStringProcessor = new QueryStringProcessor();

    @Override
    public boolean isProperHandler(Http11Request http11Request) {
        return http11Request.getUri().equals(URI) && http11Request.getHttpMethod().equals(ALLOWED_HTTP_METHOD);
    }

    @Override
    public ResponseComponent handle(Http11Request http11Request) {
        Map<String, String> queryStringDatas = queryStringProcessor.extractQueryStringDatas(http11Request.getBody());
        if (userService.addNewUser(queryStringDatas.get(ACCOUNT_KEY), queryStringDatas.get(EMAIL_KEY), queryStringDatas.get(PASSWORD_KEY))) {
            return handlerSupporter.redirectResponseComponent(REDIRECT_WHEN_REGISTER_SUCCESS, StatusCode.REDIRECT);
        }
        return handlerSupporter.redirectResponseComponent(REDIRECT_WHEN_REGISTER_FAIL, StatusCode.REDIRECT);
    }
}
