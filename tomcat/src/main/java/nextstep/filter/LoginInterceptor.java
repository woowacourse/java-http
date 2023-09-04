package nextstep.filter;

import static org.apache.coyote.http11.StaticPages.INDEX_PAGE;

import java.util.List;
import nextstep.jwp.exception.InvalidSessionException;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Session;

public class LoginInterceptor implements Interceptor {

    private static final List<String> SUPPORT_PATH = List.of(
        "/login"
    );

    @Override
    public boolean support(final HttpRequest httpRequest) {
        return SUPPORT_PATH.contains(httpRequest.getPath());
    }

    @Override
    public boolean preHandle(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        if (httpRequest.containsCookieAndJSessionID()) {
            final Session session = SessionManager.getInstance().findSession(httpRequest.getCookie().getJSessionID())
                .orElseThrow(InvalidSessionException::new);
            httpResponse.sendRedirect(INDEX_PAGE);
            return false;
        }
        return true;
    }
}
