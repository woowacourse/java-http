package nextstep.jwp.controller;

import nextstep.jwp.SessionManager;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.BusinessException;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Cookies;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;
import java.util.Arrays;
import java.util.UUID;

public class RegisterController extends AbstractController {

    private final SessionManager sessionManager;

    public RegisterController(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.setStatusCode(StatusCode.OK)
                .setContentType(ContentType.HTML)
                .setRedirect("/register.html");
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final User user = makeUser(request.getRequestBody());
        InMemoryUserRepository.save(user);
        final Session session = createSession(user);
        response.addCookie(Cookies.ofJSessionId(session.getId()));
        response.setStatusCode(StatusCode.CREATED)
                .setContentType(ContentType.HTML)
                .setRedirect("/index.html");
    }

    private User makeUser(final RequestBody requestBody) {
        final String[] tokens = requestBody.getValue().split("&");
        final String account = findValueByKey(tokens, "account");
        final String email = findValueByKey(tokens, "email");
        final String password = findValueByKey(tokens, "password");
        return new User(account, password, email);
    }

    private String findValueByKey(final String[] tokens, final String key) {
        return Arrays.stream(tokens)
                .filter(it -> it.split("=")[0]
                        .equals(key))
                .map(it -> it.split("=")[1])
                .findFirst()
                .orElseThrow(() -> new BusinessException(key + "에 대한 정보가 존재하지 않습니다."));
    }

    private Session createSession(final User user) {
        final Session session = new Session(String.valueOf(UUID.randomUUID()));
        session.addAttribute("user", user);
        sessionManager.add(session);
        return session;
    }
}
