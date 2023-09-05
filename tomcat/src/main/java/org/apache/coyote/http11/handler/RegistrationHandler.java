package org.apache.coyote.http11.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.resource.FileHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class RegistrationHandler implements HttpRequestHandler {
    @Override
    public boolean support(HttpRequest httpRequest) {
        return httpRequest.isMethodEqualTo("POST") && httpRequest.isUriEqualTo("/register");
    }

    @Override
    public void handle(HttpRequest httpRequest, OutputStream outputStream) throws IOException {
        final Map<String, String> requestBody = httpRequest.getRequestBodyAsMap();
        final User user = new User(requestBody.get("account"), requestBody.get("password"), requestBody.get("email"));
        InMemoryUserRepository.save(user);

        final HttpResponse httpResponse = new HttpResponse.Builder()
                .responseStatus("302")
                .responseBody(new FileHandler().readFromResourcePath("static/index.html"))
                .build(outputStream);
        httpResponse.flush();
    }
}
