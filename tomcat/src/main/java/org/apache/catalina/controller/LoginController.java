package org.apache.catalina.controller;

import static org.reflections.Reflections.log;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.handler.StaticResourceHandler;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class LoginController extends AbstractController {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.isSameMethod(("GET"))) {
            doGet(request, response);
            return;
        }
        if (request.isSameMethod(("POST"))) {
            doPost(request, response);
            return;
        }
        StaticResourceHandler.handleStaticResource(request, response);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (isLoggedIn(request)) {
            response.redirect("/index.html");
            return;
        }
        if (request.hasParameters()) {
            handleLogin(request, response);
            return;
        }
        StaticResourceHandler.handleStaticResource(request, response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        handleLogin(request, response);
    }

    private void handleLogin(HttpRequest request, HttpResponse response) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        if (account == null || account.isBlank() || password == null || password.isBlank()) {
            response.redirect("/401.html");
            return;
        }
        Optional<User> findUser = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
        if (findUser.isPresent()) {
            handleLoginSuccess(findUser.get(), request, response);
            return;
        }
        log.info("login failure: account= {}", account);
        response.redirect("/401.html");
    }

    private void handleLoginSuccess(User user, HttpRequest request, HttpResponse response) {
        log.info("login success: account= {}", user.getAccount());
        response.redirect("/index.html");
        String jsessionid = getOrCreateJsessionId(request, response);
        Session session = new Session(jsessionid);
        session.setAttribute("user", user);
        SessionManager.getInstance().add(session);
    }

    private String getOrCreateJsessionId(HttpRequest request, HttpResponse response) {
        String jsessionId = getJsessionId(request);
        if (jsessionId == null) {
            jsessionId = UUID.randomUUID().toString();
            response.addHeader("Set-Cookie", "JSESSIONID=" + jsessionId);
        }
        return jsessionId;
    }

    private boolean isLoggedIn(HttpRequest request) {
        String jsessionId = getJsessionId(request);
        Session session = SessionManager.getInstance().findSession(jsessionId);
        return session != null && session.getAttribute("user") != null;
    }

    private String getJsessionId(HttpRequest request) {
        String cookieHeader = request.getHeader("Cookie");
        Cookie cookie = Cookie.fromHeader(cookieHeader);
        return cookie.get("JSESSIONID");
    }
}
