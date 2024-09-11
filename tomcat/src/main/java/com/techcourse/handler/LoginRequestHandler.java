package com.techcourse.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import hoony.was.RequestHandler;
import java.util.Optional;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.QueryParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginRequestHandler implements RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginRequestHandler.class);

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.hasMethod(HttpMethod.POST) && request.hasPath("/login");
    }

    @Override
    public String handle(HttpRequest request, HttpResponse response) {
        String content = request.getContent();
        QueryParameters queryParameters = new QueryParameters(content);
        String account = queryParameters.get("account");
        String password = queryParameters.get("password");
        Optional<User> foundUser = InMemoryUserRepository.findByAccount(account);
        if (foundUser.isEmpty()) {
            return "redirect:/401.html";
        }
        User user = foundUser.get();
        if (user.checkPassword(password)) {
            log.info("Verified User : {}", user);
            Session session = request.getSession(true);
            session.setAttribute("user", user);
            response.setCookie(HttpCookie.ofJSessionId(session.getId()));
            return "redirect:/index.html";
        }
        return "redirect:/401.html";
    }
}
