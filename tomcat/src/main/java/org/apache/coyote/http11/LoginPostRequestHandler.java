package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.catalina.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginPostRequestHandler implements HttpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginPostRequestHandler.class);

    @Override
    public boolean support(final HttpRequest httpRequest) {
        return httpRequest.getRequestMethod() == RequestMethod.POST &&
                httpRequest.getRequestUrl().startsWith("/login");
    }

    @Override
    public String response(final HttpRequest httpRequest) {
        Optional<User> foundUser = InMemoryUserRepository.findByAccount(httpRequest.getParameter("account"));
        if (foundUser.isEmpty()) {
            log.info("존재하지 않는 user입니다.");
            return createRedirectResponse("http://localhost:8080/401.html");
        }

        User user = foundUser.get();
        if (!user.checkPassword(httpRequest.getParameter("password"))) {
            log.info("비밀번호 틀림");
            return createRedirectResponse("http://localhost:8080/401.html");
        }

        log.info("user = {}", user);
        Session session = httpRequest.getSession(true);
        session.setAttribute("loginUser", user);
        return createRedirectResponse("http://localhost:8080/index.html", session);
    }


    private String createRedirectResponse(final String redirectUrl) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Content-Length: " + 0 + " ",
                "Location: " + redirectUrl + " ",
                "");
    }

    private String createRedirectResponse(final String redirectUrl, final Session session) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Content-Length: " + 0 + " ",
                "Location: " + redirectUrl + " ",
                "Set-Cookie: JSESSIONID=" + session.getId() + " ",
                "");
    }
}
