package nextstep.jwp.service;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.domain.AccountLogin;
import nextstep.jwp.domain.SessionLogin;
import nextstep.jwp.domain.Token;
import nextstep.jwp.domain.UUIDStrategy;
import nextstep.jwp.exception.LoginException;
import nextstep.jwp.http.request.CookieType;
import nextstep.jwp.http.request.HttpCookie;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.HttpRequestHeader;
import nextstep.jwp.http.session.HttpSession;
import nextstep.jwp.http.session.HttpSessions;

public class LoginService implements Service {

    public boolean login(HttpRequest httpRequest) {
        if (isSessionLogin(httpRequest)) {
            return true;
        }
        final Map<String, String> queryOnURIS = httpRequest.parsedBody();
        final AccountLogin accountLogin = new AccountLogin(queryOnURIS);
        return accountLogin.isSuccess();
    }

    public boolean isSessionLogin(HttpRequest httpRequest) {
        final HttpRequestHeader httpRequestHeader = httpRequest.getHttpRequestHeader();
        final HttpCookie httpCookie = httpRequestHeader.cookie();
        final SessionLogin sessionLogin = new SessionLogin(httpCookie);
        return sessionLogin.isSuccess();
    }

    public String token(HttpRequest httpRequest) throws LoginException {
        if (isSessionLogin(httpRequest)) {
            return extractJSESSIONIDOnRequest(httpRequest);
        }
        return generateJSESSIONID(httpRequest);
    }

    private String generateJSESSIONID(HttpRequest httpRequest) {
        final UUIDStrategy uuidStrategy = new UUIDStrategy();
        final Token token = Token.fromStrategy(uuidStrategy);
        final String rawJSESSIONID = token.value();
        saveOnSession(httpRequest, rawJSESSIONID);
        return rawJSESSIONID;
    }

    private String extractJSESSIONIDOnRequest(HttpRequest httpRequest) throws LoginException {
        final HttpRequestHeader httpRequestHeader = httpRequest.getHttpRequestHeader();
        final HttpCookie httpCookie = httpRequestHeader.cookie();
        final Optional<String> valueFromKey = httpCookie.valueFromKey(
            CookieType.JSESSIONID.value());
        return valueFromKey.orElseThrow(() -> (new LoginException("[ERROR] 로그인 관련 오류가 있습니다.")));
    }

    private void saveOnSession(HttpRequest httpRequest, String rawJSESSIONID) {
        HttpSessions.putSession(rawJSESSIONID);
        final HttpSession httpSession = HttpSessions.getSession(rawJSESSIONID);
        final Map<String, String> queryOnURIS = httpRequest.parsedBody();
        final String user = queryOnURIS.get("account");
        httpSession.setAttribute("user",user);
    }
}
