package org.apache.coyote.http11.controller;


import static org.apache.coyote.http11.http.StatusCode.FOUND;
import static org.apache.coyote.http11.http.StatusCode.OK;
import static org.apache.coyote.http11.http.StatusCode.UNAUTHORIZED;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusLine;

public class LoginController extends AbstractController {


    private static final String REQUEST_URI = "/login";

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        String responseBody = readFile(httpRequest, "/login.html");
        httpResponse
                .setStatusLine(new StatusLine(httpRequest.getProtocolVersion(), OK.getNumber(), "OK"));
        httpResponse.setResponseBody(responseBody);
    }


    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        RequestBody requestBody = request.getRequestBody();
        Map<String, String> parsedBody = requestBody.getParsedBody();

        if (InMemoryUserRepository.login(parsedBody.get("account"), parsedBody.get("password"))) {
            String responseBody = readFile(request, "/index.html");
            response
                    .setStatusLine(new StatusLine(request.getProtocolVersion(), FOUND.getNumber(), "FOUND"));
            response.setResponseBody(responseBody);
            return;
        }

        String responseBody = readFile(request, "/401.html");
        response.setStatusLine(
                new StatusLine(request.getProtocolVersion(), UNAUTHORIZED.getNumber(), "UNAUTHORIZED"));
        response.setResponseBody(responseBody);
    }

    @Override
    public boolean canHandle(HttpRequest request) {
        return REQUEST_URI.equals(request.getResource());
    }


}
