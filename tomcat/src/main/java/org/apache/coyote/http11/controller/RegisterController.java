package org.apache.coyote.http11.controller;

import static org.apache.coyote.http11.http.StatusCode.OK;
import static org.apache.coyote.http11.http.StatusCode.UNAUTHORIZED;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusLine;

public class RegisterController extends AbstractController {

    private static final String REQUEST_URI = "/register";

    @Override
    public boolean canHandle(HttpRequest request) {
        return REQUEST_URI.equals(request.getResource());
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
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
    protected void doPost(HttpRequest request, HttpResponse response) {
        RequestBody requestBody = request.getRequestBody();
        Map<String, String> parsedBody = requestBody.getParsedBody();

        User user = new User(parsedBody.get("account"), parsedBody.get("password"), parsedBody.get("email"));
        System.out.println(user);
        InMemoryUserRepository.save(user);

        String responseBody = readFile(request, "/index.html");
        response
                .setStatusLine(new StatusLine(request.getProtocolVersion(), OK.getNumber(), "OK"));
        response.setResponseBody(responseBody);
    }
}
