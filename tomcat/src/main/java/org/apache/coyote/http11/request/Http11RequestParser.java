package org.apache.coyote.http11.request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Http11RequestParser {

    public HttpRequest parse(InputStream inputStream) throws IOException {
        String requestLine = readLine(inputStream);

        if (requestLine == null) {
            return null;
        }

        HttpHeaders headers = readHeaders(inputStream);
        String requestBody = readRequestBody(inputStream, headers);

        return new HttpRequest(
                new RequestLine(requestLine),
                headers,
                FormContents.from(requestBody),
                Cookies.from(headers.find(HttpHeaders.COOKIE).orElse(null))
        );
    }

    private HttpHeaders readHeaders(InputStream inputStream) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String header;

        while ((header = readLine(inputStream)) != null && !header.isEmpty()) {
            String[] nameAndValue = header.split(":", 2);
            if (nameAndValue.length == 2) {
                headers.put(
                        nameAndValue[0].trim().toLowerCase(Locale.ROOT),
                        nameAndValue[1].trim()
                );
            }
        }

        return new HttpHeaders(headers);
    }

    private String readRequestBody(InputStream inputStream, HttpHeaders headers) throws IOException {
        String contentLengthHeader = headers.find(HttpHeaders.CONTENT_LENGTH).orElse(null);

        if (contentLengthHeader == null) {
            return "";
        }

        int contentLength = Integer.parseInt(contentLengthHeader);
        byte[] body = inputStream.readNBytes(contentLength);

        return new String(body, StandardCharsets.UTF_8);
    }

    private String readLine(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int current;
        boolean previousWasCarriageReturn = false;

        while ((current = inputStream.read()) != -1) {
            if (previousWasCarriageReturn && current == '\n') {
                previousWasCarriageReturn = false;
                break;
            }

            if (previousWasCarriageReturn) {
                buffer.write('\r');
                previousWasCarriageReturn = false;
            }

            if (current == '\r') {
                previousWasCarriageReturn = true;
                continue;
            }

            buffer.write(current);
        }

        if (current == -1 && buffer.size() == 0 && !previousWasCarriageReturn) {
            return null;
        }

        if (previousWasCarriageReturn) {
            buffer.write('\r');
        }

        return buffer.toString(StandardCharsets.UTF_8);
    }
}
