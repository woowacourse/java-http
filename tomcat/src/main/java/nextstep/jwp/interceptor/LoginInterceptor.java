package nextstep.jwp.interceptor;

import org.apache.coyote.HttpCookie;
import org.apache.coyote.support.ResponseFlusher;
import org.apache.coyote.Session;
import org.apache.coyote.SessionManager;
import nextstep.jwp.support.View;
import org.apache.coyote.Headers;
import org.apache.coyote.HttpHeader;
import org.apache.coyote.HttpStatus;
import org.apache.coyote.support.Request;
import org.apache.coyote.support.Response;

import java.util.List;

public class LoginInterceptor implements Interceptor {

    private final List<String> includeUris;

    public LoginInterceptor(final List<String> includeUris) {
        this.includeUris = includeUris;
    }

    public boolean preHandle(final Request request, final Response response) {
        if (isUriNotMatch(request) || isSessionAlreadyExist(request)) {
            return true;
        }
        makeRedirect(response);
        return false;
    }

    private boolean isUriNotMatch(final Request request) {
        return !includeUris.contains(request.getUri());
    }

    private boolean isSessionAlreadyExist(final Request request) {
        return getSession(request.getHeaders()) == null;
    }

    private Session getSession(final Headers headers) {
        final String rawCookie = headers.find(HttpHeader.COOKIE);
        final HttpCookie cookie = HttpCookie.create(rawCookie);
        final String jsessionid = cookie.findByKey("JSESSIONID");
        final SessionManager sessionManager = SessionManager.get();
        return sessionManager.findSession(jsessionid);
    }

    private void makeRedirect(final Response response) {
        response.header(HttpHeader.LOCATION, View.INDEX.getValue())
                .httpStatus(HttpStatus.FOUND);
        ResponseFlusher.flush(response);
    }
}
