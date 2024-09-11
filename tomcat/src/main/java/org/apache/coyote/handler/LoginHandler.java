package org.apache.coyote.handler;

import java.util.Optional;
import java.util.UUID;

import org.apache.catalina.session.SessionManager;
import org.apache.coyote.NotFoundException;
import org.apache.coyote.UnauthorizedException;
import org.apache.http.HttpCookie;
import org.apache.http.HttpMethod;
import org.apache.catalina.session.Session;
import org.apache.http.header.HttpHeaderName;
import org.apache.http.request.HttpRequest;
import org.apache.http.response.HttpResponseGenerator;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class LoginHandler extends Handler {

    private static final LoginHandler INSTANCE = new LoginHandler();
    private final SessionManager sessionManager = SessionManager.getInstance();

    private LoginHandler() {
    }

    public static LoginHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public String handle(final HttpRequest httpRequest) {
        if (httpRequest.isSameMethod(HttpMethod.GET)) {
            return processLoginGetRequest(httpRequest);
        }

        if (httpRequest.isSameMethod(HttpMethod.POST)) {
            return processLoginPostRequest(httpRequest);
        }

        throw new NotFoundException("페이지를 찾을 수 없습니다.");
    }

    private String processLoginGetRequest(final HttpRequest httpRequest) {
        HttpCookie httpCookie = httpRequest.getHttpCookie();
        if (httpCookie == null || ! sessionManager.existsById(httpCookie.getValue("JSESSIONID"))) {
            return HttpResponseGenerator.getFoundResponse("/login.html");
        }

        return HttpResponseGenerator.getFoundResponse("/index.html");
    }

    private String processLoginPostRequest(final HttpRequest httpRequest) {
        final String account = httpRequest.getFormBodyByKey("account");
        final String password = httpRequest.getFormBodyByKey("password");

        final Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);
        if (userOptional.isEmpty() || !userOptional.get().checkPassword(password)) {
            throw new UnauthorizedException("로그인에 실패하였습니다.");
        }

        final Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", userOptional.get());
        sessionManager.add(session);
        return addCookie(
                HttpResponseGenerator.getFoundResponse("/index.html"),
                HttpCookie.from("JSESSIONID=" + session.getId()));
    }

    private String addCookie(final String response, final HttpCookie cookie) {
        return response
                .concat("\n")
                .concat(HttpHeaderName.SET_COOKIE.getValue() + ": " + cookie.toString());
    }
}
