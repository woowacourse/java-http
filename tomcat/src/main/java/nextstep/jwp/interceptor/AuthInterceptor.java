package nextstep.jwp.interceptor;

import nextstep.jwp.SessionManager;
import nextstep.jwp.handler.Handler;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Cookies;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class AuthInterceptor implements HandlerInterceptor {

    private static final String STATIC = "static";

    private final List<String> supportedPaths;
    private final SessionManager sessionManager;

    public AuthInterceptor(final List<String> paths, final SessionManager sessionManager) {
        this.supportedPaths = paths;
        this.sessionManager = sessionManager;
    }

    @Override
    public boolean preHandle(final HttpRequest request, final HttpResponse response, final Handler handler) throws IOException {
        if (isNotSupported(request.getPath())) {
            return true;
        }
        if (request.containJsessionId()) {
            final var resource = getClass().getClassLoader().getResource(STATIC + "/index.html");
            final String jsessionId = request.findJsessionId();
            final Session session = sessionManager.findSession(jsessionId);
            response.addCookie(Cookies.ofJSessionId(session.getId()));
            setResponse(response, StatusCode.OK, ContentType.HTML, resource);
            return false;
        }
        return true;
    }

    private boolean isNotSupported(final String path) {
        for (final String supportedPath : supportedPaths) {
            if (supportedPath.equals(path)) {
                return false;
            }
        }
        return true;
    }

    private void setResponse(final HttpResponse response, final StatusCode statusCode,
                             final ContentType contentType, final URL resource) throws IOException {
        response.setStatusCode(statusCode);
        response.setContentType(contentType);
        response.setResponseBodyByUrl(resource);
    }
}
