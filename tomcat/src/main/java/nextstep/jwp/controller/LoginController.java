package nextstep.jwp.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.unauthorized.UserAccountException;
import nextstep.jwp.exception.unauthorized.UserPasswordException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final SessionManager SESSION_MANAGER = new SessionManager();

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        Map<String, Object> parameters = httpRequest.getRequestBodyParameters();
        final String account = (String) parameters.get("account");
        final String password = (String) parameters.get("password");

        User user = getUser(account);

        if (!user.checkPassword(password)) {
            throw new UserPasswordException("user 의 " + account + "에 해당하는 " + password + "가 아닙니다.");
        }

        log.info("로그인 성공! 아이디: " + user.getAccount());
        SESSION_MANAGER.setUserSession(httpResponse, user);

        httpResponse.found("/index.html");
    }

    private User getUser(String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new UserAccountException(account + " account에 해당하는 유저는 존재하지 않습니다."));
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        Optional<Session> session = SESSION_MANAGER.getSession(httpRequest);
        if (session.isPresent() && session.get().getUserAttribute().isPresent()) {
            httpResponse.found("/index.html");
            return;
        }

        String responseBody = getBody();
        httpResponse.ok(responseBody);
    }

    private String getBody() throws IOException {
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        File file = new File(resource.getFile());
        Path path = file.toPath();
        return new String(Files.readAllBytes(path));
    }
}


