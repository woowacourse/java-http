package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemorySessionRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.auth.Session;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.service.LoginService;

public class LoginController extends AbstractController {
    private static final String SESSION_ID_KEY = "JSESSIONID";
    private static final LoginController INSTANCE = new LoginController();

    private final LoginService loginService = LoginService.getInstance();

    private LoginController() {
    }

    public static LoginController getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean canHandle(String url) {
        return url.contains("login");
    }

    @Override
    void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new UnsupportedOperationException(httpRequest.getRequestUri() + "지원하지 않는 요청입니다.");
    }

    @Override
    void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        if(httpRequest.isQueryStringRequest()){
            checkLogin(httpRequest, httpResponse);
            return;
        }

        if(httpRequest.hasCookie() && httpRequest.getCookie().has(SESSION_ID_KEY)){
            checkSession(httpRequest, httpResponse);
            return;
        }

        loginView(httpResponse);

    }

    private void loginView(HttpResponse httpResponse) {
        httpResponse.statusCode(StatusCode.OK_200)
                .viewUrl("/login.html");
    }

    private void checkSession(HttpRequest httpRequest, HttpResponse httpResponse) {
        Cookie cookie = httpRequest.getCookie();
        String jsessionid = cookie.getByKey(SESSION_ID_KEY);

        if (!InMemorySessionRepository.existsById(jsessionid)) {
            throw new SecurityException("잘못된 세션 정보입니다.");
        }

        httpResponse.redirect("/index.html");
    }

    private void checkLogin(HttpRequest httpRequest, HttpResponse httpResponse) {
        Map<String, String> parameters = httpRequest.getQueryParameters();
        loginService.checkLogin(parameters.get("account"), parameters.get("password"));
        User user = loginService.findByAccount(parameters.get("account"));
        Cookie userSessionCookie = makeUserSessionCookie(user);

        httpResponse.redirect("/index.html")
                .setCookie(userSessionCookie);
    }

    private Cookie makeUserSessionCookie(User user) {
        Session session = new Session();
        session.setAttribute("user", user);
        InMemorySessionRepository.save(session);
        return new Cookie(Map.of(SESSION_ID_KEY, session.getId()));
    }
}
