package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpRequest {
    private final String method;
    private final String requestTarget;

    public HttpRequest(String method, String requestTarget) {
        this.method = method;
        this.requestTarget = requestTarget;
    }

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        final var requestLine = bufferedReader.readLine();
        if(requestLine.isBlank()) {
            throw new IllegalArgumentException("Request line is blank");
        }
        final var parts = requestLine.trim().split(" ");

        return new HttpRequest(parts[0], parts[1]);
    }

    public boolean isGetMethod() {
        return method.equals("GET");
    }

    public boolean isPath(String path){
        return requestTarget.equals(path);
    }

    public String getMethod() {
        return method;
    }

    public String getRequestTarget() {
        return requestTarget;
    }
}
