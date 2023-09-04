package org.apache.coyote.handler;

import org.apache.coyote.Handler;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.response.ContentType;
import org.apache.coyote.http.response.HttpStatusCode;
import org.apache.coyote.http.response.Response;
import org.apache.coyote.http.util.HttpMethod;

public class WelcomeHandler implements Handler {

    private static final String WELCOME_PAGE_CONTENT = "Hello World!";

    private final String rootContextPath;

    public WelcomeHandler(final String rootContextPath) {
        this.rootContextPath = rootContextPath;
    }

    @Override
    public boolean supports(final Request request) {
        return isGetMethod(request) && isWelcomePageRequest(request);
    }

    private boolean isGetMethod(final Request request) {
        return request.matchesByMethod(HttpMethod.GET);
    }

    private boolean isWelcomePageRequest(final Request request) {
        return request.isWelcomePageRequest(rootContextPath);
    }

    @Override
    public Response service(final Request request) {
        return Response.of(request, HttpStatusCode.OK, ContentType.TEXT_HTML, WELCOME_PAGE_CONTENT);
    }
}
