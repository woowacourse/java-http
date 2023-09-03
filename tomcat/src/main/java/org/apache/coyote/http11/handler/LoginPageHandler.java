package org.apache.coyote.http11.handler;

import nextstep.jwp.model.User;
import org.apache.coyote.http11.FileHandler;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestHandler;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.session.SessionManager;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

public class LoginPageHandler implements HttpRequestHandler {

    @Override
    public boolean support(HttpRequest httpRequest) {
        return httpRequest.isMethodEqualTo("GET") && httpRequest.isUriEqualTo("/login") && !httpRequest.hasQueryParameter();
    }

    @Override
    public void handle(HttpRequest httpRequest, OutputStream outputStream) throws IOException {
        final Optional<String> sessionId = httpRequest.getSessionId();
        if (sessionId.isPresent() && SessionManager.hasSessionWithAttributeType(sessionId.get(), User.class)) {
            final HttpResponse httpResponse = new HttpResponse.Builder()
                    .responseStatus("302")
                    .responseBody(new FileHandler().readFromResourcePath("static/index.html"))
                    .build(outputStream);
            httpResponse.flush();
        }

        final HttpResponse httpResponse = new HttpResponse.Builder()
                .responseBody(new FileHandler().readFromResourcePath("static/login.html"))
                .build(outputStream);
        httpResponse.flush();
    }
}
