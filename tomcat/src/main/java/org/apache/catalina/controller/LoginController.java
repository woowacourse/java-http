package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.Cookies;
import org.apache.coyote.http11.Request;
import org.apache.coyote.http11.Response;

public class LoginController extends AbstractController {

    @Override
    protected void doGet(Request request, Response response) throws Exception {
        final Cookies cookies = request.getCookies();
        if (cookies != null && cookies.hasValue("JSESSIONID")) {
            final String sessionId = cookies.getValue("JSESSIONID");
            if (SessionManager.getInstance().contains(sessionId)) {
                response.sendRedirection("/index.html");
                return;
            }
        }
        response.sendResource("/login.html");
    }

    @Override
    protected void doPost(Request request, Response response) throws Exception {
        final String[] split = request.getMessageBody().split("&");
        final String account = split[0].split("=")[1];
        final String password = split[1].split("=")[1];
        InMemoryUserRepository.findByAccount(account).ifPresentOrElse(
                user -> {
                    if (user.checkPassword(password)) {
                        final String sessionId = UUID.randomUUID().toString();
                        final Session session = new Session(sessionId);
                        session.setAttribute("user", user);
                        SessionManager.getInstance().add(session);
                        final Cookies responseCookies = new Cookies(Map.of("JSESSIONID", sessionId));
                        response.addCookies(responseCookies);
                        try {
                            response.sendRedirection("/index.html");
                        } catch (IOException e) {
                            throw new UncheckedServletException(e);
                        }
                        return;
                    }
                    try {
                        response.sendRedirection("/401.html");
                    } catch (IOException e) {
                        throw new UncheckedServletException(e);
                    }
                },
                () -> {
                    try {
                        response.sendRedirection("/401.html");
                    } catch (IOException e) {
                        throw new UncheckedServletException(e);
                    }
                }
        );
    }
}
