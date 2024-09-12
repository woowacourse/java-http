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

public class LoginController implements Controller {

    public static LoginController INSTANCE = new LoginController();

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
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        if(httpRequest.isQueryStringRequest()){
            checkLogin(httpRequest, httpResponse);
        }

        if(httpRequest.hasCookie() && httpRequest.getCookie().has("JSESSIONID")){
            checkSession(httpRequest, httpResponse);
        }

        loginView(httpResponse);
    }

    public void loginView(HttpResponse httpResponse) {
        httpResponse.statusCode(StatusCode.OK_200)
                .viewUrl("/login.html");
    }

    public void checkSession(HttpRequest httpRequest, HttpResponse httpResponse) {
        Cookie cookie = httpRequest.getCookie();
        String jsessionid = cookie.getByKey("JSESSIONID");

        if (!InMemorySessionRepository.existsById(jsessionid)) {
            throw new SecurityException("잘못된 세션 정보입니다.");
        }

        httpResponse.redirect("/index.html");
    }

    public void checkLogin(HttpRequest httpRequest, HttpResponse httpResponse) {
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
        return new Cookie(Map.of("JSESSIONID", session.getId()));
    }
}
