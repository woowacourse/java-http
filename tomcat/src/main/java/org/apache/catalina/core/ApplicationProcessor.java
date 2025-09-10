package org.apache.catalina.core;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.util.request.HttpRequest;
import org.apache.coyote.util.response.HttpContentTypeResolver;
import org.apache.coyote.util.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationProcessor {

    private static final Logger log = LoggerFactory.getLogger(ApplicationProcessor.class);

    public static HttpResponse processLogin(HttpRequest httpRequest) {
        if ("GET".equals(httpRequest.getMethod())) {
            Session existingSession = httpRequest.getSession(false);
            if (existingSession != null && getUser(existingSession) != null) {
                return HttpResponse.redirect("/index.html");
            }
            try {
                final String path = "static/login.html";
                final byte[] body = readResource(path);
                final String contentType = HttpContentTypeResolver.resolve(path);
                return HttpResponse.of("HTTP/1.1 200 OK", contentType, body);
            } catch (IOException e) {
                return HttpResponse.internalServerError();
            }
        }
        Session existingSession = httpRequest.getSession(false);
        if (existingSession != null && getUser(existingSession) != null) {
            return HttpResponse.redirect("/index.html");
        }
        String account = httpRequest.getQueryValue("account").orElse(null);
        String password = httpRequest.getQueryValue("password").orElse(null);
        if (account == null || password == null) {
            return HttpResponse.redirect("401.html");
        }
        Optional<User> userOpt = InMemoryUserRepository.findByAccount(account);
        if (userOpt.isEmpty() || !userOpt.get().checkPassword(password)) {
            return HttpResponse.redirect("401.html");
        }
        User user = userOpt.get();
        final Session session = httpRequest.changeSessionId();
        session.setAttribute("user", user);
        HttpResponse response = HttpResponse.redirect("/index.html");
        response.addCookie(SessionManager.JSESSIONID, session.getId());
        log.info("User: {}", user);
        return response;
    }

    public static HttpResponse processRegister(HttpRequest request) {
        String account = request.getQueryValue("account").orElse(null);
        String email = request.getQueryValue("email").orElse(null);
        String password = request.getQueryValue("password").orElse(null);
        if (account == null || email == null || password == null) {
            return HttpResponse.redirect("static/404.html");
        }
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info("회원가입 성공! 아이디: {}", user.getAccount());
        return HttpResponse.redirect("/index.html");
    }

    private static byte[] readResource(String path) throws IOException {
        try (InputStream inputStream = ApplicationProcessor.class.getClassLoader().getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + path);
            }
            return inputStream.readAllBytes();
        }
    }

    private static User getUser(Session session) {
        return (User) session.getAttribute("user");
    }
}
