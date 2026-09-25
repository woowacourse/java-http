package org.apache.coyote.http11.model;

import java.io.BufferedReader;
import java.io.IOException;

public record RequestLine(
        String httpMethod,
        String requestUrl,
        String protocolVersion
) {

    public static RequestLine from(BufferedReader reader) throws IOException {
        String[] request = reader.readLine().trim().split("\\s+");
        return new RequestLine(request[0], request[1], request[2]);
    }
}
