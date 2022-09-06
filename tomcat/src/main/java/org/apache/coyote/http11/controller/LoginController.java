package org.apache.coyote.http11.controller;


import static org.apache.coyote.http11.http.StatusCode.FOUND;
import static org.apache.coyote.http11.http.StatusCode.OK;
import static org.apache.coyote.http11.http.StatusCode.UNAUTHORIZED;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusLine;

public class LoginController extends AbstractController {


    private static final String REQUEST_URI = "/login";

    @Override
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        try {
            if (!httpRequest.hasQueryString()) {
                String responseBody = readFile(httpRequest, "/login.html");
                httpResponse
                        .setStatusLine(new StatusLine(httpRequest.getProtocolVersion(), OK.getNumber(), "OK"));
                httpResponse.setResponseBody(responseBody);
                return;
            }

            Map<String, String> queries = httpRequest.getQueries();
            if (InMemoryUserRepository.login(queries.get("account"), queries.get("password"))) {
                String responseBody = readFile(httpRequest, "/index.html");
                httpResponse
                        .setStatusLine(new StatusLine(httpRequest.getProtocolVersion(), FOUND.getNumber(), "FOUND"));
                httpResponse.setResponseBody(responseBody);
            }
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
