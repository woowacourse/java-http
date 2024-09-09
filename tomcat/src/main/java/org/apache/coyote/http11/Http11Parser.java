package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Http11Parser {

    private static final String REQUEST_HEADER_SUFFIX = "";

    public static Http11Request readHttpRequest(final BufferedReader bufferedReader) throws IOException {
        List<String> lines = new ArrayList<>();
        String line = bufferedReader.readLine();
        if (line.isEmpty()) {
            throw new IllegalArgumentException("HttpRequest가 비어있습니다.");
        }
        while (!REQUEST_HEADER_SUFFIX.equals(line)) {
            lines.add(line);
            line = bufferedReader.readLine();
        }
        return new Http11Request(lines);
    }
}
