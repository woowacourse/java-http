package nextstep.jwp.handler;

import nextstep.Application;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.QueryStrings;
import org.apache.coyote.http11.enums.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public HttpResponse login(HttpRequest httpRequest) {
        if (httpRequest.isQueryStringEmpty()) {
            return HttpResponse.of(HttpStatus.OK, "/login.html");
        }

        final QueryStrings queryStrings = httpRequest.getQueryStrings();
        final User user = findUser(queryStrings);

        final String password = queryStrings.findByKey(PASSWORD);
        if (user.checkPassword(password)) {
            log.info(user.toString());
            return generateSuccessResponse();
        }

        return HttpResponse.of(HttpStatus.UNAUTHORIZED, "/401.html");
    }

    private User findUser(final QueryStrings queryStrings) {
        final String account = queryStrings.findByKey(ACCOUNT);
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new UserNotFoundException(account));
    }

    private HttpResponse generateSuccessResponse() {
        final HttpResponse response = HttpResponse.of(HttpStatus.FOUND, "/login.html");
        response.addHeader("Location", "/index.html");
        return response;
    }
}
