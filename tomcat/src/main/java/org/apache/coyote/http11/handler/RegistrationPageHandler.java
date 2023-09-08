package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.resource.FileHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;
import java.io.OutputStream;

public class RegistrationPageHandler implements HttpRequestHandler {
    @Override
    public boolean support(HttpRequest httpRequest) {
        return httpRequest.isMethodEqualTo(HttpMethod.GET) && httpRequest.isUriEqualTo("/register");
    }

    @Override
    public void handle(HttpRequest httpRequest, OutputStream outputStream) throws IOException {
        final HttpResponse httpResponse = new HttpResponse.Builder()
                .responseBody(new FileHandler().readFromResourcePath("static/register.html"))
                .build(outputStream);

        httpResponse.flush();
    }
}
