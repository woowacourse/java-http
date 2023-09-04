package nextstep.jwp.handler.get;

import nextstep.jwp.exception.BusinessException;
import org.apache.coyote.http11.Handler;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.request.ContentType;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
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
    public Http11Response resolve(final Http11Request request) throws IOException {
        if (request.notContainJsessionId()) {
            final var resource = getClass().getClassLoader().getResource(STATIC + "/login.html");
            return makeHttp11Response(resource, StatusCode.OK);
        }
        final String jsessionId = request.findJsessionId();
        final Session session = sessionManager.findSession(jsessionId);
        validateSession(session);
        final var resource = getClass().getClassLoader().getResource(STATIC + "/index.html");
        return makeHttp11Response(resource, StatusCode.FOUND);
    }

    private Http11Response makeHttp11Response(final URL resource, final StatusCode statusCode) throws IOException {
        final var actualFilePath = new File(resource.getPath()).toPath();
        final var fileBytes = Files.readAllBytes(actualFilePath);
        final String responseBody = new String(fileBytes, StandardCharsets.UTF_8);
        return new Http11Response(statusCode, ContentType.findByPath(resource.getPath()), responseBody);
    }

    private void validateSession(final Session session) {
        if (session == null) {
            throw new BusinessException("세션이 적절하지 않습니다.");
        }
    }
}
