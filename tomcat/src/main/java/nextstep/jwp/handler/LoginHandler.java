package nextstep.jwp.handler;

import java.util.Objects;
import java.util.Optional;
import nextstep.Application;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestBody;
import org.apache.coyote.http11.HttpRequestHeader;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.enums.HttpMethod;
import org.apache.coyote.http11.enums.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private final Manager manager;

    public LoginHandler(final Manager manager) {
        this.manager = manager;
    }

    public HttpResponse login(final HttpRequest httpRequest) {
        if (httpRequest.isSameHttpMethod(HttpMethod.GET)) {
            return HttpResponse.of(httpRequest, HttpStatus.OK, "/login.html");
        }

        final HttpRequestHeader requestHeader = httpRequest.getHttpRequestHeader();
        final HttpRequestBody requestBody = httpRequest.getHttpRequestBody();
        final User user = findUser(requestHeader, requestBody);

        final String password = requestBody.findByKey(PASSWORD);
        if (user.checkPassword(password)) {
            log.info(user.toString());
            return generateSuccessResponse(httpRequest);
        }

        return HttpResponse.of(httpRequest, HttpStatus.UNAUTHORIZED, "/401.html");
    }

    private User findUser(final HttpRequestHeader requestHeader, final HttpRequestBody requestBody) {
        Optional<String> jSessionId = requestHeader.findJSessionId();
        if (jSessionId.isEmpty()) {
            final String account = requestBody.findByKey(ACCOUNT);
            return InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new UserNotFoundException(account));
        }

        final String id = jSessionId.get();
        final Session session = manager.findSession(id);
        if (Objects.isNull(session)) {
            throw new UserNotFoundException(id);
        }

        return session.getUser();
    }

    private HttpResponse generateSuccessResponse(final HttpRequest httpRequest) {
        final HttpResponse response = HttpResponse.of(httpRequest, HttpStatus.FOUND, "/login.html");
        response.addHeader("Location", "/index.html");
        return response;
    }
}
