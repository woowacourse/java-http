package org.apache.coyote.http11.request;

import java.io.IOException;
import java.util.List;

public class FakeRequestReader implements RequestReader {

    @Override
    public String readRequestLine() throws IOException {
        return "POST /login HTTP/1.1";
    }

    @Override
    public List<String> readRequestHeaders() throws IOException {
        return List.of(
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded");
    }

    @Override
    public String readRequestBody(int contentLength) throws IOException {
        return "account=gugu&password=password";
    }
}
