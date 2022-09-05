package nextstep.jwp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private static final SessionManager sessionManager = new SessionManager();

    private AuthController() {
    }

    public static HttpResponse login(HttpRequest request) {
        if (request.hasSession()) {
            Session session = request.getSession();
            User user = getUser(sessionManager.findSession(session.getId()));
            if (InMemoryUserRepository.findByAccount(user.getAccount())
                .isPresent()) {
                return HttpResponse.redirect(request, "/index.html");
            }

        }

        if (!request.hasQuery()) {
            return new HttpResponse(request, StatusCode.OK, getStaticResource(request.getUrl()));
        }
        Session session = new Session(new HttpCookie().getCookieValue("JSESSIONID"));

        final String account = request.getQueryValue("account");
        final String password = request.getQueryValue("password");

        try {
            User user = loginUser(account, password);
            session.setAttribute("user", user);
            sessionManager.add(session);
            log.info("로그인 성공! 아이디: {}", user.getAccount());
        } catch (IllegalArgumentException e) {
            return HttpResponse.redirect(request, "/401.html");
        }

        HttpResponse response = HttpResponse.redirect(request, "/index.html");
        response.setCookie(HttpCookie.fromJSESSIONID(session.getId()));
        return response;
    }

    private static User getUser(Session session) {
        return (User)session.getAttribute("user");
    }

    public static HttpResponse signUp(HttpRequest request) {
        final String account = request.getQueryValue("account");
        final String password = request.getQueryValue("password");
        final String email = request.getQueryValue("email");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info("회원가입 성공! 아이디: {}", user.getAccount());
        return HttpResponse.redirect(request, "/index.html");
    }

    private static String getStaticResource(URL url) {
        try {
            return new String(Files.readAllBytes(new File(Objects.requireNonNull(url)
                .getFile())
                .toPath()));
        } catch (IOException e) {
            throw new IllegalArgumentException("No such resource");
        }
    }

    private static User loginUser(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
            .orElseThrow(() -> new IllegalArgumentException("Account Not Found"));

        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("Password Not Matched");
        }
        return user;
    }
}
