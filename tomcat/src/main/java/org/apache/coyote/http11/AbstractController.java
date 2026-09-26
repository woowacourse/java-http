package org.apache.coyote.http11;

import java.util.UUID;

public abstract class AbstractController implements Controller {

    protected static final String JSESSIONID = "JSESSIONID";
    private static final String SET_COOKIE_HEADER = "Set-Cookie";

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.getMethod().equals("POST")) {
            doPost(request, response);
            return;
        }
        if (request.getMethod().equals("GET")) {
            doGet(request, response);
        }
    }

    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
    }

    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
    }

    protected void setOkResponse(final HttpResponse response, final String contentType, final byte[] body,
                                 final String setCookie) {
        response.setStatus(200, "OK ");
        if (!setCookie.isEmpty()) {
            response.setHeader(SET_COOKIE_HEADER, setCookie + " ");
        }
        response.setHeader("Content-Type", contentType + " ");
        response.setHeader("Content-Length", body.length + " ");
        response.setBody(body);
    }

    protected void setRedirectResponse(final HttpResponse response, final String location, final String setCookie) {
        response.setStatus(302, "Found ");
        if (!setCookie.isEmpty()) {
            response.setHeader(SET_COOKIE_HEADER, setCookie + " ");
        }
        response.setHeader("Location", location + " ");
        response.setHeader("Content-Length", "0 ");
    }

    protected String createSetCookieHeader(final String sessionId) {
        if (sessionId != null) {
            return "";
        }
        return JSESSIONID + "=" + UUID.randomUUID();
    }
}
