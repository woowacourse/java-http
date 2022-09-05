package nextstep.jwp.handler;

import java.util.Optional;
import nextstep.Application;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestBody;
import org.apache.coyote.http11.HttpRequestHeader;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.enums.HttpMethod;
import org.apache.coyote.http11.enums.HttpStatus;
import org.apache.coyote.http11.utils.UuidUtil;
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

        final HttpRequestBody requestBody = httpRequest.getBody();
        final Optional<User> findUser = findUser(requestBody);

        if (findUser.isEmpty()) {
            return HttpResponse.of(httpRequest, HttpStatus.UNAUTHORIZED, "/401.html");
        }

        final User user = findUser.get();
        final String password = requestBody.findByKey(PASSWORD);
        if (user.checkPassword(password)) {
            log.info(user.toString());
            setUpSession(user, httpRequest.getHeaders());
            return generateSuccessResponse(httpRequest);
        }

        return HttpResponse.of(httpRequest, HttpStatus.UNAUTHORIZED, "/401.html");
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

    private Optional<User> findUser(final HttpRequestBody requestBody) {
        final String account = requestBody.findByKey(ACCOUNT);
        return InMemoryUserRepository.findByAccount(account);
    }

    private HttpResponse generateSuccessResponse(final HttpRequest httpRequest) {
        final HttpResponse response = HttpResponse.of(httpRequest, HttpStatus.FOUND, "/login.html");
        response.addHeader("Location", "/index.html");
        return response;
    }
}
