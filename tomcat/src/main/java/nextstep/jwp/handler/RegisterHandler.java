package nextstep.jwp.handler;

import java.util.Optional;
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

public class RegisterHandler {

    private final Manager manager;

    public RegisterHandler(final Manager manager) {
        this.manager = manager;
    }

    public HttpResponse register(final HttpRequest httpRequest) {
        if (httpRequest.isSameHttpMethod(HttpMethod.GET)) {
            return HttpResponse.of(httpRequest, HttpStatus.OK, "/register.html");
        }

        final HttpRequestBody requestBody = httpRequest.getHttpRequestBody();
        final User user = createUser(requestBody);
        InMemoryUserRepository.save(user);

        setUpSession(user, httpRequest.getHttpRequestHeader());

        final HttpResponse response = HttpResponse.of(httpRequest, HttpStatus.FOUND, "/register.html");
        response.addHeader("Location", "/login.html");
        return response;
    }

    private User createUser(final HttpRequestBody requestBody) {
        final String account = requestBody.findByKey("account");
        final String password = requestBody.findByKey("password");
        final String email = requestBody.findByKey("email");
        return new User(account, password, email);
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
