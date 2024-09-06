package org.apache.coyote.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.AbstractController;
import org.apache.coyote.exception.UnauthorizedException;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.common.Cookies;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.util.HttpResponseBuilder;

public final class MemberController extends AbstractController {

    @Override
    public void service(HttpRequest request, HttpResponse httpResponse) {

        if (request.getMethod() == HttpMethod.GET) {
            doGet(request, httpResponse);
            return;
        }
        if (request.getMethod() == HttpMethod.POST) {
            doPost(request, httpResponse);
        }
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse httpResponse) {
        if (request.getUri().equals("/login")) {
            login(request, httpResponse);
            return;
        }
        if (request.getUri().equals("/register")) {
            register(request, httpResponse);
        }
    }

    private void login(HttpRequest request, HttpResponse httpResponse) {
        String account = request.getParams("account");
        String password = request.getParams("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(UnauthorizedException::new);
        if (user.checkPassword(password)) {
            HttpResponseBuilder.setRedirection(httpResponse, "/");
            registerLoginSession(request, httpResponse, user);
            return;
        }
        HttpResponseBuilder.setRedirection(httpResponse, "/401.html");
    }

    private void registerLoginSession(HttpRequest request, HttpResponse httpResponse, User user) {
        Cookies requestCookies = request.getCookies();
        if (!requestCookies.hasJSESSIONID() || SessionManager.isRegisitered(requestCookies.getJSESSIONID())) {
            Session loginSession = new Session();
            loginSession.setAttribute("user", user);
            SessionManager.add(loginSession);
            Cookies cookies = new Cookies();
            cookies.setCookie("JSESSIONID", loginSession.getId());
            httpResponse.setCookies(cookies);
        }
    }

    private void register(HttpRequest request, HttpResponse httpResponse) {
        String account = request.getParams("account");
        String password = request.getParams("password");
        String email = request.getParams("email");

        InMemoryUserRepository.save(new User(account, password, email));

        HttpResponseBuilder.setRedirection(httpResponse, "/");
    }
}
