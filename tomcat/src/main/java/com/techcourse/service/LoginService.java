package com.techcourse.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.dto.LoginDto;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.catalina.Session;
import org.apache.coyote.http11.message.common.HttpCookie;
import org.apache.coyote.http11.message.parser.HttpCookieParser;
import org.apache.coyote.http11.message.request.HttpRequest;

public class LoginService {

    private static final String USER_SESSION_NAME = "user";

    public boolean alreadyLogin(HttpRequest request) {
        String cookies = request.getCookies();
        HttpCookie httpCookie = HttpCookieParser.parse(cookies);
        if (httpCookie.hasSessionId()) {
            Session session = request.getSession(false);
            return session != null && session.getAttribute(USER_SESSION_NAME) != null;
        }
        return false;
    }

    public User findLoginUser(LoginDto loginDto) {
        Optional<User> loginUser = InMemoryUserRepository.findByAccount(loginDto.account());

        if (loginUser.isPresent()) {
            User user = loginUser.get();
            if (user.checkPassword(loginDto.password())) {
                return user;
            }
        }
        return null;
    }
}
