package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

class HttpRequestConvertor {

    public static final String HEADER_DELIMITER = ":";

    public HttpRequest convert(final InputStream inputStream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        final String requestLine = reader.readLine();
        final Map<String, String> headers = parseHeaders(reader);
        final String body = parseBody(reader, headers);

        return new HttpRequest(requestLine, headers, body);
    }

    private Map<String, String> parseHeaders(final BufferedReader reader) throws IOException {
        final Map<String, String> headers = new HashMap<>();

        String headerLine = reader.readLine();
        while (!headerLine.equals("")) {
            headers.put(headerName(headerLine), headerValue(headerLine));
            headerLine = reader.readLine();
        }

        return headers;
    }

    private static String headerName(final String headerLine) {
        return headerLine.split(HEADER_DELIMITER)[0].trim();
    }

    private static String headerValue(final String headerLine) {
        return headerLine.split(HEADER_DELIMITER)[1].trim();
    }

    private String parseBody(final BufferedReader reader, final Map<String, String> headers) throws IOException {
        if (headers.containsKey("Content-Length")) {
            final int contentLength = Integer.parseInt(headers.get("Content-Length"));
            final char[] body = new char[contentLength];
            reader.read(body, 0, contentLength);
            return new String(body);
        }
        return "";
    }
}
