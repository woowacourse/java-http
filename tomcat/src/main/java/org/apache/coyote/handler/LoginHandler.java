package org.apache.coyote.handler;

import static org.apache.coyote.response.ContentType.HTML;
import static org.apache.coyote.response.StatusCode.FOUND;

import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.NoSuchUserException;
import nextstep.jwp.model.User;
import org.apache.coyote.request.QueryParams;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.Location;
import org.apache.coyote.session.Cookie;
import org.apache.coyote.session.Cookies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    private LoginHandler() {
    }

    public static HttpResponse login(final String requestBody, final Cookies cookies) {
        final QueryParams queryParams = QueryParams.from(requestBody);

        final String account = queryParams.getValueFromKey("account");
        final String password = queryParams.getValueFromKey("password");

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(NoSuchUserException::new);

        if (!user.checkPassword(password)) {
            return HttpResponse.of(FOUND, HTML, Location.from("/401.html"));
        }

        final String userInformation = user.toString();
        log.info(userInformation);

        final Optional<Cookie> cookie = cookies.getCookie("JSESSIONID");
        if (cookie.isEmpty()) {
            final Cookie jsessionid = new Cookie("JSESSIONID", UUID.randomUUID().toString());
            return HttpResponse.of(FOUND, HTML, Location.from("/index.html"), jsessionid);
        }

        return HttpResponse.of(FOUND, HTML, Location.from("/index.html"));
    }
}
