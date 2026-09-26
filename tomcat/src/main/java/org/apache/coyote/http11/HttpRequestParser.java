package org.apache.coyote.http11;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.request.RequestParams;

public final class HttpRequestParser {

    private HttpRequestParser() {}

    public static HttpRequest parse(final String readLine, final InputStream inputStream)
            throws IOException {
        final var requestLine = new RequestLine(readLine);
        final var params = new RequestParams(requestLine.queryString());
        final var headers = readHeaders(inputStream);
        final var body = inputStream.readNBytes(headers.contentLength());
        return new HttpRequest(requestLine, params, headers, body);
    }

    public static String readLine(final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream line = new ByteArrayOutputStream();
        int value;
        while ((value = inputStream.read()) != -1 && value != '\n') {
            line.write(value);
        }
        if (value == -1 && line.size() == 0) {
            return null;
        }
        final String result = line.toString(UTF_8);
        if (result.endsWith("\r")) {
            return result.substring(0, result.length() - 1);
        }
        return result;
    }

    private static HttpHeaders readHeaders(final InputStream inputStream) throws IOException {
        final Map<String, String> headers = new LinkedHashMap<>();
        String headerLine;
        while ((headerLine = readLine(inputStream)) != null && !headerLine.isEmpty()) {
            final int index = headerLine.indexOf(":");
            if (index > 0) {
                final String name = headerLine.substring(0, index).strip();
                final String value = headerLine.substring(index + 1).strip();
                headers.put(name, value);
            }
        }
        return new HttpHeaders(headers);
    }
}
