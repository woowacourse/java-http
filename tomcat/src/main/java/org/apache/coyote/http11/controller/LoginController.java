package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> queryParameters = request.getQueryParameter();
        String account = queryParameters.get("account");
        String password = queryParameters.get("password");
        Optional<User> foundUser = InMemoryUserRepository.findByAccount(account);

        if (foundUser.isEmpty() || !foundUser.get().checkPassword(password)) {
            response.redirect("/401.html", "");
            return;
        }
        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", foundUser.get());
        SessionManager.add(session);
        response.redirect("/index.html", "JSESSIONID=" + session.getId());

    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        HttpCookie httpCookie = new HttpCookie(request.getHeaders().get("Cookie"));
        Session session = SessionManager.findSession(httpCookie.getJSessionId());

        if (session != null) {
            log.info("로그인 페이지 접근! 세션 아이디: {}", session.getId());
            response.redirect("/index", "");
            return;
        }
        String resource = getStaticResource(request.getPath());
        response.ok(resource, getContentType(request.getPath()));
    }

    private String getContentType(String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "text/css;charset=utf-8 ";
        }
        if (requestUri.endsWith(".js")) {
            return "text/javascript;charset=utf-8 ";
        }
        return "text/html;charset=utf-8 ";
    }

    @Nullable
    private String getStaticResource(String requestUri) throws IOException {
        URL url = getClass().getClassLoader().getResource("static" + requestUri);
        if (url == null) {
            requestUri = requestUri + ".html";
            url = getClass().getClassLoader().getResource("static" + requestUri);
        }
        if (url != null) {
            try (InputStream inputStream = url.openStream()) {
                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }
        }
        return null;
    }
}


