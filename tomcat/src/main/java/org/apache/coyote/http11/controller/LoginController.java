package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.UuidGenerator;
import org.apache.coyote.http11.model.request.FormParameters;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private final SessionManager sessionManager;

    public LoginController(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    protected void doGet(HttpRequest request, Http11Response response) throws Exception {
        Session session = getSession(request);
        if (session != null && session.getAttribute("user") != null) {
            response.redirect("/index.html");
            return;
        }
        String url = request.getRequestPath() + ".html";
        String resourcePath = "static" + url;
        URL resource = LoginController.class.getClassLoader().getResource(resourcePath);
        Path path = Path.of(resource.toURI());
        response.setBody(Files.readAllBytes(path));
        response.ok("text/html");
    }

    @Override
    protected void doPost(HttpRequest request, Http11Response response) {
        User user;
        try {
            FormParameters formParameters = FormParameters.from(request.getRequestBody().value());
            user = findUser(formParameters.values());
        } catch (IllegalArgumentException e) {
            response.redirect("/401.html");
            return;
        }
        Session session = getSession(request);
        boolean created = session == null;
        if (created) {
            session = new Session(UuidGenerator.generate());
            sessionManager.add(session);
        }

        session.setAttribute("user", user);
        log.info("로그인 사용자: {}", user.getAccount());
        response.redirect("/index.html");
        if (created) {
            response.addHeader("Set-Cookie", "JSESSIONID=" + session.getId());
        }
    }

    private static User findUser(Map<String, String> values) {
        checkUserParameter(values);
        String account = values.get("account");
        String password = values.get("password");
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 입니다."));
        validateUser(user, password);
        return user;
    }

    private static void validateUser(User user, String password) {
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("잘못된 회원정보 입니다.");
        }
    }

    private static void checkUserParameter(Map<String, String> values) {
        if (!values.containsKey("account") || !values.containsKey("password")) {
            throw new IllegalArgumentException("회원정보를 찾지 못했습니다.");
        }
    }

    private Session getSession(HttpRequest request) {
        String jsessionid = request.getRequestHeader().cookie().getCookie("JSESSIONID");
        Session session;
        if (jsessionid == null) {
            session = null;
        } else {
            session = sessionManager.findSession(jsessionid);
        }
        return session;
    }
}
