package org.apache.coyote.http11;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public HttpResponse handle(final HttpRequest request) {
        if (request.isGetRequest()) {
            return handleGetMethod(request);
        }
        return handlePostMethod(request);
    }

    private HttpResponse handleGetMethod(final HttpRequest request) {
        return new HttpResponse(StatusCode.OK, ContentType.TEXT_HTML.getValue(), ViewLoader.from("/login.html"));
    }

    private HttpResponse handlePostMethod(final HttpRequest request) {
        final Map<String, String> requestBody = request.getRequestBody();
        if (requestBody == null || requestBody.size() != 2) {
            return HttpResponse.toUnauthorized();
        }
        final String account = requestBody.get("account");
        final String password = requestBody.get("password");
        final boolean loginSuccess = login(account, password);
        if (loginSuccess) {
            return new HttpResponse(StatusCode.FOUND, ContentType.TEXT_HTML.getValue(), ViewLoader.toIndex());
        }
        return HttpResponse.toUnauthorized();
    }

    private boolean login(final String account, final String password) {
        final Optional<User> userOpt = InMemoryUserRepository.findByAccount(account);
        if (userOpt.isPresent()) {
            final User user = userOpt.get();
            if (user.checkPassword(password)) {
                log.info("user : {}", user);
                return true;
            }
        }
        return false;
    }
}
