package org.apache.catalina.connector;

import static org.apache.catalina.session.SessionManager.JSESSIONID_COOKIE_NAME;

import java.io.IOException;
import org.apache.catalina.handle.RequestHandlerResolver;
import org.apache.catalina.session.Session;
import org.apache.coyote.Adapter;
import org.apache.coyote.http11.data.Cookie;
import org.apache.coyote.http11.data.HttpRequest;
import org.apache.coyote.http11.data.HttpResponse;

public class CoyoteAdapter implements Adapter {

    private final RequestHandlerResolver requestHandlerResolver;

    public CoyoteAdapter(
            final RequestHandlerResolver requestHandlerResolver
    ) {
        this.requestHandlerResolver = requestHandlerResolver;
    }

    @Override
    public void service(
            final HttpRequest request,
            final HttpResponse response
    ) throws IOException {
        requestHandlerResolver.resolve(request)
                .handle(request, response);

        applySessionCookie(request, response);
    }

    private void applySessionCookie(
            final HttpRequest request,
            final HttpResponse response
    ) {
        final Session session = request.getSession(false);

        if (session == null) {
            return;
        }

        final Cookie cookie = Cookie.create(
                JSESSIONID_COOKIE_NAME,
                session.getId(),
                "/"
        );

        response.getCookies().addCookie(cookie);
    }
}
