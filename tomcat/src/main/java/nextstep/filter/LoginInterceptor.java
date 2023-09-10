package nextstep.filter;

import static org.apache.coyote.http11.StaticPages.INDEX_PAGE;

import java.util.List;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class LoginInterceptor implements Interceptor {

    private static final List<String> support_path = List.of(
        "/login"
    );

    @Override
    public boolean support(final HttpRequest httpRequest) {
        return support_path.contains(httpRequest.getPath());
    }

    @Override
    public boolean preHandle(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        if (httpRequest.containsCookieAndJSessionID()) {
            SessionManager.getInstance().validateSession(httpRequest.getCookie());
            httpResponse.sendRedirect(INDEX_PAGE);
            return false;
        }
        return true;
    }
}
