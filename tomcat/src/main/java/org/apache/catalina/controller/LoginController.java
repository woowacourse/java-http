package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.exception.CommonException;
import org.apache.coyote.http11.session.Session;

public class LoginController extends AbstractController {

    StaticResourceHandler staticResourceHandler  = new StaticResourceHandler();

    @Override
    protected void doGet(
            HttpRequest httpRequest,
            HttpResponse httpResponse
    ) throws IOException {
        String uri = httpRequest.uri();

        if ("/login".equals(uri)) {
            Session session = httpRequest.getSession();
            if (session != null && session.getAttribute("user") != null) {
                httpResponse.setStatusCode(HttpStatus.FOUND);
                httpResponse.setHeader("Location", "http://localhost:8080");
                return;
            }
            staticResourceHandler.serve(httpRequest, httpResponse);
        }
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        String uri = httpRequest.uri();

        if ("/login".equals(uri)) {
            login(httpRequest, httpResponse);
        } else if ("/register".equals(uri)) {
            register(httpRequest, httpResponse);
        }
    }

    public void login(
            HttpRequest httpRequest,
            HttpResponse httpResponse
    ) {
        String account = httpRequest.getForm("account");
        String password = httpRequest.getForm("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new CommonException(HttpStatus.UNAUTHORIZED));

        if (user.checkPassword(password)) {
            Session session = httpRequest.getSession();
            session.setAttribute("user", user);
            httpResponse.addCookie("SID", session.getId());
            httpResponse.setStatusCode(HttpStatus.FOUND);
            httpResponse.setHeader("Location", "http://localhost:8080");
            System.out.println(account + " 로그인 완료");
            return;
        }
        throw new CommonException(HttpStatus.UNAUTHORIZED);
    }

    public void register(
            HttpRequest httpRequest,
            HttpResponse httpResponse
    ) {

        String account = httpRequest.getForm("account");
        String email = httpRequest.getForm("email");
        String password = httpRequest.getForm("password");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        httpResponse.setStatusCode(HttpStatus.FOUND);
        httpResponse.setHeader("Location", "http://localhost:8080");
    }
}
