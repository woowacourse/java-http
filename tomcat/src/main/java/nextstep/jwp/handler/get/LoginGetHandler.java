package nextstep.jwp.handler.get;

import nextstep.jwp.SessionManager;
import nextstep.jwp.exception.BusinessException;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Handler;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class LoginGetHandler implements Handler {

    private static final String STATIC = "static";

    private final SessionManager sessionManager;

    public LoginGetHandler(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public HttpResponse resolve(final HttpRequest request) throws IOException {
        if (request.notContainJsessionId()) {
            final var resource = getClass().getClassLoader().getResource(STATIC + "/login.html");
            return HttpResponse.createBy(request.getVersion(), resource, StatusCode.OK);
        }
        final String jsessionId = request.findJsessionId();
        final Session session = sessionManager.findSession(jsessionId);
        validateSession(session);
        final var resource = getClass().getClassLoader().getResource(STATIC + "/index.html");
        return HttpResponse.createBy(request.getVersion(), resource, StatusCode.FOUND);
    }

    private void validateSession(final Session session) {
        if (session == null) {
            throw new BusinessException("세션이 적절하지 않습니다.");
        }
    }
}
