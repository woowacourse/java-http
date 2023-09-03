package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.FileHandler;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestHandler;
import org.apache.coyote.http11.HttpResponse;

import java.io.IOException;
import java.io.OutputStream;

public class LoginPageHandler implements HttpRequestHandler {

    @Override
    public boolean support(HttpRequest httpRequest) {
        return httpRequest.isMethodEqualTo("GET") && httpRequest.isUriEqualTo("/login") && !httpRequest.hasQueryParameter();
    }

    @Override
    public void handle(HttpRequest httpRequest, OutputStream outputStream) throws IOException {
        final HttpResponse httpResponse = new HttpResponse.Builder()
                .responseBody(new FileHandler().readFromResourcePath("static/login.html"))
                .build(outputStream);
        httpResponse.flush();
    }
}
