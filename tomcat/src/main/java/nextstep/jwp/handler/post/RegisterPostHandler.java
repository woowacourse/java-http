package nextstep.jwp.handler.post;

import nextstep.jwp.SessionManager;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.BusinessException;
import nextstep.jwp.handler.Handler;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Cookies;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.UUID;

public class RegisterPostHandler implements Handler {

    private static final String STATIC = "static";

    private final SessionManager sessionManager;

    public RegisterPostHandler(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws IOException {
        final User user = makeUser(request.getRequestBody());
        InMemoryUserRepository.save(user);
        final Session session = new Session(String.valueOf(UUID.randomUUID()));
        session.addAttribute("user", user);
        sessionManager.add(session);
        response.addCookie(Cookies.ofJSessionId(session.getId()));
        final var resource = getClass().getClassLoader().getResource(STATIC + "/index.html");
        setResponse(response, StatusCode.CREATED, ContentType.HTML, resource);
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

    private void setResponse(final HttpResponse response, final StatusCode statusCode,
                             final ContentType contentType, final URL resource) throws IOException {
        response.setStatusCode(statusCode);
        response.setContentType(contentType);
        response.setResponseBodyByUrl(resource);
    }
}
