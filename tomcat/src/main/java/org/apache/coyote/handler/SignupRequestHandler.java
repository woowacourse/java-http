package org.apache.coyote.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.ResourceReader;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class SignupRequestHandler extends AbstractRequestHandler {

    private static final String SESSION_ID_COOKIE_NAME = "JSESSIONID";

    @Override
    protected void get(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setStatus(HttpStatus.OK);
        httpResponse.setHeader("Content-Type", "text/html;charset=utf-8");
        httpResponse.setBody(ResourceReader.readFile(httpRequest.getRequestURI()));
    }

    @Override
    protected void post(HttpRequest httpRequest, HttpResponse httpResponse) {
        Map<String, String> param = httpRequest.getParsedBody();
        User newUser = new User(param.get("account"), param.get("password"), param.get("email"));
        validateExists(newUser);
        InMemoryUserRepository.save(newUser);
        Session session = httpRequest.getSession();
        session.setAttribute("user", newUser);
        SessionManager.getInstance().add(session);

        httpResponse.setStatus(HttpStatus.FOUND);
        httpResponse.setHeader("Set-Cookie", SESSION_ID_COOKIE_NAME + "=" + session.getId());
        httpResponse.setHeader("Location", "/index.html");
        httpResponse.setBody(ResourceReader.readFile(httpRequest.getRequestURI()));
    }

    private void validateExists(User user) {
        if (InMemoryUserRepository.findByAccount(user.getAccount()).isPresent()) {
            throw new UncheckedServletException(new IllegalStateException("이미 존재하는 아이디 입니다."));
        }
    }
}
