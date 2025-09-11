package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class LoginPostRequestHandler implements HttpRequestHandler {


    private static final Logger log = LoggerFactory.getLogger(LoginPostRequestHandler.class);

    @Override
    public boolean support(final HttpRequest httpRequest) {
        return httpRequest.getRequestMethod() == RequestMethod.POST &&
                httpRequest.getRequestUrl()
                        .startsWith("/login");
    }

    @Override
    public void response(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        Optional<User> foundUser = InMemoryUserRepository.findByAccount(httpRequest.getParameter("account"));
        if (foundUser.isEmpty()) {
            log.info("존재하지 않는 user입니다.");
            httpResponse.redirect("http://localhost:8080/401.html");
            return;
        }

        User user = foundUser.get();
        if (!user.checkPassword(httpRequest.getParameter("password"))) {
            log.info("비밀번호 틀림");
            httpResponse.redirect("http://localhost:8080/401.html");
            return;
        }

        log.info("user = {}", user);
        Session session = httpRequest.getSession(true);
        session.setAttribute("loginUser", user);

        httpResponse.redirect("http://localhost:8080/index.html")
                .addHeader("Set-Cookie", "JSESSIONID=" + session.getId());
    }
}
