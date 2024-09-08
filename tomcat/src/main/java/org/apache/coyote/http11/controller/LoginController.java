package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemorySessionRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.auth.Session;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBuilder;
import org.apache.coyote.http11.service.LoginService;


public class LoginController implements Controller {

    private final LoginService loginService = new LoginService();

    @Override
    public boolean canHandle(String url) {
        return url.contains("login");
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        if (httpRequest.isQueryStringRequest()) {
            return checkLogin(httpRequest);
        }

        if (httpRequest.hasCookie() && httpRequest.getCookie().has("JSESSIONID")) {
            return checkSession(httpRequest);
        }

        return new ResponseBuilder()
                .statusCode(HttpStatusCode.OK_200)
                .viewUrl("/login.html")
                .build();
    }

    private HttpResponse checkSession(HttpRequest httpRequest) {
        Cookie cookie = httpRequest.getCookie();
        String jsessionid = cookie.getByKey("JSESSIONID");

        if(!InMemorySessionRepository.existsById(jsessionid)){
            throw new SecurityException("잘못된 세션 정보입니다.");
        }

        return new ResponseBuilder()
                .statusCode(HttpStatusCode.FOUND_302)
                .location("/index.html")
                .build();
    }

    private HttpResponse checkLogin(HttpRequest httpRequest) {
        Map<String, String> parameters = httpRequest.getQueryParameters();
        loginService.checkLogin(parameters.get("account"), parameters.get("password"));

        User user = loginService.findByAccount(parameters.get("account"));
        Cookie userSessionCookie = makeUserSessionCookie(user);

        return new ResponseBuilder()
                .statusCode(HttpStatusCode.FOUND_302)
                .location("/index.html")
                .setCookie(userSessionCookie)
                .build();
    }

    private Cookie makeUserSessionCookie(User user) {
        Session session = new Session();
        session.setAttribute("user", user);
        InMemorySessionRepository.save(session);
        return new Cookie(Map.of("JSESSIONID", session.getId()));
    }
}
