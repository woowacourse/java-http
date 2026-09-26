package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        Session session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            found(response, "/index.html");
            return;
        }

        ok(response, ResourceLoader.CONTENT_TYPE_HTML, ResourceLoader.loadResponseBody("/login.html"));
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        if (!isFormUrlEncoded(request)) {
            unsupportedMediaType(response);
            return;
        }

        Map<String, String> userInfo = request.getFormParameters();
        if (hasBlankParameter(userInfo, "account", "password")) {
            badRequest(response);
            return;
        }

        Optional<User> user = login(userInfo);
        if (user.isPresent()) {
            Session session = request.getSession(true);
            session.setAttribute("user", user.get());

            found(response, "/index.html");
            response.addCookie(HttpCookie.ofJSessionId(session.getId()));
            return;
        }

        found(response, "/401.html");
    }

    private boolean isFormUrlEncoded(HttpRequest request) {
        return request.getHeaders()
                .getOrDefault("Content-Type", "")
                .equals("application/x-www-form-urlencoded");
    }

    private boolean hasBlankParameter(Map<String, String> parameters, String... requiredKeys) {
        for (String key : requiredKeys) {
            String value = parameters.get(key);
            if (value == null || value.isBlank()) {
                return true;
            }
        }

        return false;
    }

    private Optional<User> login(Map<String, String> parameters) {
        String account = parameters.getOrDefault("account", "");
        String password = parameters.getOrDefault("password", "");

        Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isEmpty()) {
            return Optional.empty();
        }

        if (user.get().checkPassword(password)) {
            log.info("user : {}", user.get());
            return user;
        }

        return Optional.empty();
    }
}
