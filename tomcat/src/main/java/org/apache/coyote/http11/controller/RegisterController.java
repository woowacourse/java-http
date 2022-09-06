package org.apache.coyote.http11.controller;

import static org.apache.coyote.http11.http.StatusCode.OK;
import static org.apache.coyote.http11.http.StatusCode.UNAUTHORIZED;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusLine;

public class RegisterController extends AbstractController {

    private static final String REQUEST_URI = "/register";

    @Override
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        try {
            String responseBody = readFile(httpRequest, "/register.html");
            httpResponse
                    .setStatusLine(new StatusLine(httpRequest.getProtocolVersion(), OK.getNumber(), "OK"));
            httpResponse.setResponseBody(responseBody);
        } catch (Exception e) {
            String responseBody = readFile(httpRequest, "/401.html");
            httpResponse.setStatusLine(
                    new StatusLine(httpRequest.getProtocolVersion(), UNAUTHORIZED.getNumber(), "UNAUTHORIZED"));
            httpResponse.setResponseBody(responseBody);
        }
    }

    @Override
    public boolean canHandle(HttpRequest request) {
        return REQUEST_URI.equals(request.getResource());
    }
}
