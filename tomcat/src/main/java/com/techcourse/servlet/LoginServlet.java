package com.techcourse.servlet;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.catalina.servlet.AbstractHttpServlet;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.QueryParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginServlet extends AbstractHttpServlet {

    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        String content = request.getContent();
        QueryParameters queryParameters = new QueryParameters(content);
        String account = queryParameters.get("account");
        String password = queryParameters.get("password");
        Optional<User> foundUser = InMemoryUserRepository.findByAccount(account);
        if (foundUser.isEmpty()) {
            response.redirectTo("/401.html");
            return;
        }
        User user = foundUser.get();
        if (user.checkPassword(password)) {
            log.info("Verified User : {}", user);
            Session session = request.getSession(true);
            session.setAttribute("user", user);
            response.setCookie(HttpCookie.ofJSessionId(session.getId()));
            response.setContentType(ContentType.TEXT_HTML);
            response.redirectTo("/index.html");
            return;
        }
        response.redirectTo("/401.html");
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        Session session = request.getSession(true);
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.redirectTo("/login.html");
            return;
        }
        response.redirectTo("/index.html");
    }
}
