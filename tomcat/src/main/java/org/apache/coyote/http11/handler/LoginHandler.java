package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.util.Map;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.HttpCookies;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler extends AbstractRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {
        response.setStaticResourceResponse(request);
        response.addHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=utf-8");
        response.write();
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> params = request.getParams();
        String account = params.get("account");
        String password = params.get("password");

        try {
            User user = InMemoryUserRepository.findByAccount(account)
                    .filter(it -> it.checkPassword(password))
                    .orElseThrow(() -> new IllegalArgumentException("로그인 실패"));

            log.debug("user: {}", user);

            if (request.sessionNotExists()) {
                Session session = request.getSession(true);
                session.setAttribute("user", user);
                HttpCookies cookie = new HttpCookies();
                cookie.setCookie(HttpCookies.JSESSIONID, session.getId());
                response.addHeader(HttpHeaders.SET_COOKIE, cookie.getCookieString());
            }
            response.sendRedirect("/index.html");
        } catch (IllegalArgumentException e) {
            response.sendRedirect("/401.html");
        }

        response.write();
    }
}
