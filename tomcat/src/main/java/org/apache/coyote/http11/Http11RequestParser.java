package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;

public class Http11RequestParser {

    public static Http11Request parse(final BufferedReader reader) throws IOException {
        final RequestLine requestLine = new RequestLine(reader.readLine());
        final Headers headers = new Headers();
        String headerLine;
        while (!(headerLine = reader.readLine()).isEmpty()) {
            headers.addHeader(headerLine);
        }
        return new Http11Request(requestLine, headers);
    }
}
