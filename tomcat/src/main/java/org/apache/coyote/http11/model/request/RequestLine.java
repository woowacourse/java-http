package org.apache.coyote.http11.model.request;

import java.io.BufferedReader;
import java.io.IOException;

public record RequestLine(
        String httpMethod,
        UriInfo uriInfo,
        String protocolVersion
) {

    public static RequestLine from(BufferedReader reader) throws IOException {
        String[] request = reader.readLine().trim().split("\\s+");
        return new RequestLine(request[0], UriInfo.makeUriInfo(request[1]), request[2]);
    }
}
