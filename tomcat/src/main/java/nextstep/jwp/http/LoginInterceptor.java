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

    public boolean preHandle(final Request request, final OutputStream outputStream) {
        if (!includeUris.contains(request.getUri())) {
            return true;
        }
        final Session session = getSession(request.getHeaders());
        if(session == null) {
            return true;
        }
        makeRedirect(outputStream);
        return false;
    }

    private Session getSession(final Headers headers) {
        final String rawCookie = headers.find(HttpHeader.COOKIE);
        final HttpCookie cookie = HttpCookie.create(rawCookie);
        final String jsessionid = cookie.findByKey("JSESSIONID");
        final SessionManager sessionManager = SessionManager.get();
        return sessionManager.findSession(jsessionid);
    }

    private void makeRedirect(final OutputStream outputStream) {
        final Headers headers = new Headers();
        headers.put(HttpHeader.LOCATION, View.INDEX.getValue());
        final Response response = new Response(headers).httpStatus(HttpStatus.FOUND);
        ResponseFlusher.flush(outputStream, response);
    }
}
