package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Optional;
import java.util.StringTokenizer;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.message.common.Cookie;
import org.apache.coyote.http11.message.common.CookieManager;
import org.apache.coyote.http11.message.common.HttpCookie;
import org.apache.coyote.http11.ViewResolver;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.StatusCode;

public class LoginController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        String cookies = request.getCookies();
        HttpCookie httpCookie = new HttpCookie(cookies);
        if (httpCookie.hasKey("JSESSIONID")) {
            response.addHeader("Content-Type", "text/html;charset=utf-8");
            response.sendRedirect("http://localhost:8080/index.html");
            return;
        }
        response.setStatus(StatusCode.OK);
        response.addHeader("Content-Type", "text/html;charset=utf-8");
        response.setBody(ViewResolver.getInstance().resolveViewName("/login.html"));
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        StringTokenizer tokenizer = new StringTokenizer(request.getBody(), "=|&");
        String account = "";
        String password = "";
        while (tokenizer.hasMoreTokens()) {
            String key = tokenizer.nextToken();
            if (key.equals("account") && tokenizer.hasMoreTokens()) {
                account = tokenizer.nextToken();
            } else if (key.equals("password") && tokenizer.hasMoreTokens()) {
                password = tokenizer.nextToken();
            }
        }
        Optional<User> loginUser = InMemoryUserRepository.findByAccount(account);
        if (loginUser.isPresent()) {
            User user = loginUser.get();
            if (user.checkPassword(password)) {
                log.info("로그인 성공! 아이디: {}", user.getAccount());

                String cookies = request.getCookies();
                HttpCookie httpCookie = new HttpCookie(cookies);
                if (httpCookie.hasKey("JSESSIONID")) {
                    response.addHeader("Content-Type", "text/html;charset=utf-8");
                    response.sendRedirect("http://localhost:8080/index.html");
                    return;
                }
                Cookie cookie = CookieManager.setCookie();
                Session session = new Session(cookie.getValue());
                session.setAttribute("user", user);
                SessionManager.getInstance().add(session);
                response.addHeader("Content-Type", "text/html;charset=utf-8");
                response.sendRedirect("http://localhost:8080/index.html");
                response.addHeader("Set-Cookie", cookie.getKeyValue());
                return;
            }
        }
        response.addHeader("Content-Type", "text/html;charset=utf-8");
        response.sendRedirect("http://localhost:8080/401.html");
    }
}
