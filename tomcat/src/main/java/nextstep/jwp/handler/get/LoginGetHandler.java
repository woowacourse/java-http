package nextstep.jwp.handler.get;

import nextstep.jwp.SessionManager;
import nextstep.jwp.exception.BusinessException;
import nextstep.jwp.handler.Handler;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import java.io.IOException;
import java.net.URL;

public class LoginGetHandler implements Handler {

    private static final String STATIC = "static";

    private final SessionManager sessionManager;

    public LoginGetHandler(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws IOException {
        if (request.notContainJsessionId()) {
            final var resource = getClass().getClassLoader().getResource(STATIC + "/login.html");
            setResponse(response, StatusCode.OK, ContentType.HTML, resource);
            return;
        }
        final String jsessionId = request.findJsessionId();
        final Session session = sessionManager.findSession(jsessionId);
        validateSession(session);
        final var resource = getClass().getClassLoader().getResource(STATIC + "/index.html");
        setResponse(response, StatusCode.FOUND, ContentType.HTML, resource);
    }

    private void setResponse(final HttpResponse response, final StatusCode statusCode,
                             final ContentType contentType, final URL resource) throws IOException {
        response.setStatusCode(statusCode);
        response.setContentType(contentType);
        response.setResponseBodyByUrl(resource);
    }

    private void validateSession(final Session session) {
        if (session == null) {
            throw new BusinessException("세션이 적절하지 않습니다.");
        }
    }
}
