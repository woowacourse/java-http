package org.apache.coyote.http11.http11handler;

import java.util.Map;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.dto.ResponseComponent;
import org.apache.coyote.http11.http11handler.login.LoginService;
import org.apache.coyote.http11.http11handler.support.HandlerSupporter;
import org.apache.coyote.http11.http11handler.support.QueryStringProcessor;
import org.apache.coyote.http11.http11request.Http11Request;
import org.slf4j.Logger;

public class LoginPageHandler implements Http11Handler {

    private static final String URI = "/login";
    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";
    private static final String REDIRECT_WHEN_LOGIN_SUCCESS = "/index.html";
    private static final String REDIRECT_WHEN_LOGIN_FAIL = "/401.html";

    private QueryStringProcessor queryStringProcessor = new QueryStringProcessor();
    private HandlerSupporter handlerSupporter = new HandlerSupporter();
    private LoginService loginService = new LoginService();

    @Override
    public boolean isProperHandler(Http11Request http11Request) {
        String uri = queryStringProcessor.removeQueryString(http11Request.getUri());
        return uri.equals(URI);
    }

    @Override
    public ResponseComponent handle(Logger log, String uri) {
        if (queryStringProcessor.existQueryString(uri)) {
            Map<String, String> queryStrings = queryStringProcessor.extractQueryString(uri);
            return makeResponseComponentAccordingToLogin(queryStrings);
        }

        uri = handlerSupporter.addHtmlExtension(uri);
        return handlerSupporter.extractElements(uri, StatusCode.OK);
    }

    private ResponseComponent makeResponseComponentAccordingToLogin(Map<String, String> queryStrings) {
        if (loginService.login(queryStrings.get(ACCOUNT_KEY), queryStrings.get(PASSWORD_KEY))) {
            return handlerSupporter.extractElements(REDIRECT_WHEN_LOGIN_SUCCESS, StatusCode.FOUND);
        }
        return handlerSupporter.extractElements(REDIRECT_WHEN_LOGIN_FAIL, StatusCode.UNAUTHORIZED);
    }
}
