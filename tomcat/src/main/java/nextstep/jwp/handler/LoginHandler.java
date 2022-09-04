package nextstep.jwp.handler;

import nextstep.Application;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestBody;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.enums.HttpMethod;
import org.apache.coyote.http11.enums.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public HttpResponse login(final HttpRequest httpRequest) {
        if (httpRequest.isSameHttpMethod(HttpMethod.GET)) {
            return HttpResponse.of(HttpStatus.OK, "/login.html");
        }

        final HttpRequestBody requestBody = httpRequest.getHttpRequestBody();
        final User user = findUser(requestBody);

        final String password = requestBody.findByKey(PASSWORD);
        if (user.checkPassword(password)) {
            log.info(user.toString());
            return generateSuccessResponse();
        }

        return HttpResponse.of(HttpStatus.UNAUTHORIZED, "/401.html");
    }

    private User findUser(final HttpRequestBody requestBody) {
        final String account = requestBody.findByKey(ACCOUNT);
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new UserNotFoundException(account));
    }

    private HttpResponse generateSuccessResponse() {
        final HttpResponse response = HttpResponse.of(HttpStatus.FOUND, "/login.html");
        response.addHeader("Location", "/index.html");
        return response;
    }
}
