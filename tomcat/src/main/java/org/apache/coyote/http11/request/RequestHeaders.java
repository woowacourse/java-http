package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RequestHeaders {

    private final List<RequestHeader> headers = new ArrayList<>();

    public RequestHeaders(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        while (line != null && !line.isEmpty()) {
            headers.add(new RequestHeader(line));
            line = reader.readLine();
        }
    }

    public Optional<RequestHeader> getContentLength() {
        return get("Content-Length");
    }

    public Optional<RequestHeader> get(String key) {
        return headers.stream()
                .filter(header -> header.hasKey(key))
                .findAny();
    }
}
