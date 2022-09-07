package nextstep.jwp.handler;

import java.util.Optional;
import nextstep.Application;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnAuthorizedException;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.coyote.http11.enums.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.request.HttpRequestHeader;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.utils.UuidUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private final Manager manager;

    public LoginController(final Manager manager) {
        this.manager = manager;
    }

    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        if (httpRequest.isGetMethod()) {
            return doGet(httpRequest);
        }

        return doPost(httpRequest);
    }

    private HttpResponse doGet(final HttpRequest httpRequest) {
        Optional<String> jSessionId = httpRequest.getHeaders()
                .findJSessionId();

        if (jSessionId.isEmpty()) {
            return HttpResponse.of(httpRequest, HttpStatusCode.OK, "/login.html");
        }

        return generateSuccessResponse(httpRequest);
    }

    private HttpResponse generateSuccessResponse(final HttpRequest httpRequest) {
        final HttpResponse response = HttpResponse.of(httpRequest, HttpStatusCode.FOUND, "/login.html");
        response.addLocation("/index.html");
        return response;
    }

    private HttpResponse doPost(final HttpRequest httpRequest) {
        try {
            return login(httpRequest);
        } catch (UserNotFoundException | UnAuthorizedException e) {
            log.error(e.getMessage(), e);
            return HttpResponse.of(httpRequest, HttpStatusCode.UNAUTHORIZED, "/401.html");
        }
    }

    private HttpResponse login(final HttpRequest httpRequest) {
        final HttpRequestBody requestBody = httpRequest.getBody();

        final String account = requestBody.findByKey(ACCOUNT);
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new UserNotFoundException(account));

        validatePassword(user, requestBody);

        setUpSession(user, httpRequest.getHeaders());

        return generateSuccessResponse(httpRequest);
    }

    private void validatePassword(final User user, final HttpRequestBody requestBody) {
        final String password = requestBody.findByKey(PASSWORD);

        if (!user.checkPassword(password)) {
            throw new UnAuthorizedException(user.getAccount());
        }
    }

    private void setUpSession(final User user, final HttpRequestHeader httpRequestHeader) {
        Optional<String> jSessionId = httpRequestHeader.findJSessionId();
        if (jSessionId.isEmpty()) {
            addSession(user, UuidUtil.randomUuidString());
            return;
        }
        addSession(user, jSessionId.get());
    }

    private void addSession(final User user, final String jSessionId) {
        Session session = new Session(jSessionId);
        session.addUser(user);
        manager.add(session);
    }
}
