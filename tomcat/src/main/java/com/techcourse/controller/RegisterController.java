package com.techcourse.controller;

import org.apache.catalina.Session;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        
        if (account == null || password == null || email == null) {
            return HttpResponse.redirect("/register.html");
        }
        
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return HttpResponse.redirect("/register.html");
        }
        
        User newUser = new User(account, password, email);
        InMemoryUserRepository.save(newUser);
        
        // 회원가입 후 자동 로그인
        Session session = request.getSession(true);
        session.setAttribute("user", newUser);
        String setCookieHeader = HttpCookie.createJSessionIdSetCookieHeader(session.getId());
        HttpResponse response = HttpResponse.redirect("/index.html");
        response.addHeader("Set-Cookie", setCookieHeader);
        return response;
    }
}