package org.apache.coyote.http11;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final int LOGIN_PARAMETER_SIZE = 2;

    @Override
    public HttpResponse handle(final HttpRequest request) {
        if (request.isGetRequest()) {
            return handleGetMethod(request);
        }
        return handlePostMethod(request);
    }

    private HttpResponse handleGetMethod(final HttpRequest request) {
        if (request.hasJSessionId() && Objects.nonNull(request.getSession(false))) {
            final Session session = request.getSession(false);
            final User user = (User) session.getAttribute("user");
            if (Objects.nonNull(user)) {
                HttpResponse httpResponse = new HttpResponse(StatusCode.FOUND, ContentType.TEXT_HTML.getValue(),
                        ViewLoader.toIndex());
                httpResponse.sendRedirect("/index.html");
                return httpResponse;
            }
        }
        return new HttpResponse(StatusCode.OK, ContentType.TEXT_HTML.getValue(), ViewLoader.from("/login.html"));
    }

    private HttpResponse handlePostMethod(final HttpRequest request) {
        final Map<String, String> requestBody = request.getRequestBody();
        if (Objects.isNull(requestBody) || requestBody.size() != LOGIN_PARAMETER_SIZE) {
            return HttpResponse.toUnauthorized();
        }
        final String account = requestBody.get("account");
        final String password = requestBody.get("password");
        final User user = login(account, password);

        if (Objects.nonNull(user)) {
            HttpResponse httpResponse = new HttpResponse(StatusCode.FOUND, ContentType.TEXT_HTML.getValue(),
                    ViewLoader.toIndex());
            final Session session = request.getSession(true);
            session.setAttribute("user", user);
            httpResponse.addCookie(session.getId());
            httpResponse.sendRedirect("/index.html");
            return httpResponse;
        }
        return HttpResponse.toUnauthorized();
    }

    private User login(final String account, final String password) {
        final Optional<User> userOpt = InMemoryUserRepository.findByAccount(account);
        if (userOpt.isPresent()) {
            final User user = userOpt.get();
            if (user.checkPassword(password)) {
                log.info("user : {}", user);
                return user;
            }
        }
        return null;
    }
}
