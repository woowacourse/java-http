package nextstep.jwp.http;

import nextstep.jwp.support.View;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.http.HttpHeader;
import org.apache.http.HttpStatus;

import java.io.OutputStream;
import java.util.List;

public class LoginInterceptor {

    private final List<String> includeUris;

    public LoginInterceptor(final List<String> includeUris) {
        this.includeUris = includeUris;
    }

    public boolean preHandle(final Request request, final Response response, final OutputStream outputStream) {
        if (isUriNotMatch(request) || isSessionAlreadyExist(request)) {
            return true;
        }
        makeRedirect(response, outputStream);
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

    private void makeRedirect(final Response response, final OutputStream outputStream) {
        response.header(HttpHeader.LOCATION, View.INDEX.getValue())
                .httpStatus(HttpStatus.FOUND);
        ResponseFlusher.flush(outputStream, response);
    }
}
