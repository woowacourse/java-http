package nextstep.jwp.handler;

import java.util.Optional;
import nextstep.Application;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnAuthorizedException;
import nextstep.jwp.exception.UserNotFoundException;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.enums.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.request.HttpRequestHeader;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.utils.UuidUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private static final Controller INSTANCE = new LoginController();

    public static Controller getInstance() {
        return INSTANCE;
    }

    private LoginController() {
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        Optional<String> jSessionId = httpRequest.getHeaders()
                .findJSessionId();

        if (jSessionId.isEmpty()) {
            return HttpResponse.of(HttpStatusCode.OK, "/login.html");
        }

        return generateSuccessResponse();
    }

    private HttpResponse generateSuccessResponse() {
        final HttpResponse response = HttpResponse.of(HttpStatusCode.FOUND, "/login.html");
        response.addLocation("/index.html");
        return response;
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) {
        try {
            return login(httpRequest);
        } catch (UserNotFoundException | UnAuthorizedException e) {
            log.error(e.getMessage(), e);
            return HttpResponse.of(HttpStatusCode.UNAUTHORIZED, "/401.html");
        }
    }

    private HttpResponse login(final HttpRequest httpRequest) {
        final HttpRequestBody requestBody = httpRequest.getBody();

        final String account = requestBody.findByKey(ACCOUNT);
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new UserNotFoundException(account));

        validatePassword(user, requestBody);

        setUpSession(user, httpRequest.getHeaders());

        final HttpResponse response = generateSuccessResponse();
        response.addJSessionId(UuidUtil.randomUuidString());
        return response;
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
        SessionManager.getInstance().add(session);
    }
}
