package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Http11RequestParser {

    public HttpRequest parse(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String requestLine = reader.readLine();

        if (requestLine == null) {
            return null;
        }

        HttpHeaders headers = readHeaders(reader);
        String requestBody = readRequestBody(reader, headers);

        return new HttpRequest(
                new RequestLine(requestLine),
                headers,
                FormContents.from(requestBody),
                Cookies.from(headers.find(HttpHeaders.COOKIE).orElse(null))
        );
    }

    private HttpHeaders readHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String header;

        while ((header = reader.readLine()) != null && !header.isEmpty()) {
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

    private String readRequestBody(BufferedReader reader, HttpHeaders headers) throws IOException {
        String contentLengthHeader = headers.find(HttpHeaders.CONTENT_LENGTH).orElse(null);

        if (contentLengthHeader == null) {
            return "";
        }

        int contentLength = Integer.parseInt(contentLengthHeader);
        char[] buffer = new char[contentLength];

        int offset = 0;
        while (offset < contentLength) {
            int readCount = reader.read(buffer, offset, contentLength - offset);

            if (readCount == -1) {
                break;
            }

            offset += readCount;
        }

        return new String(buffer, 0, offset);
    }
}
