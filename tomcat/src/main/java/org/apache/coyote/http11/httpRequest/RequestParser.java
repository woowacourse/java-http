package org.apache.coyote.http11.httpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestParser {

    public static HttpRequest parse(
            final BufferedReader br
    ) throws IOException {
        final RequestLine requestLine = RequestLine.parse(br.readLine());

        final Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = br.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            final String[] headerTokens = line.split(": ");
            headers.put(headerTokens[0], headerTokens[1]);
        }

        RequestBody requestBody = RequestBody.empty();
        if (requestLine.getRequestMethod() == RequestMethod.POST) {
            final String length = headers.get("Content-Length");
            if (length != null) {
                final int contentLength = Integer.parseInt(length);
                final char[] buffer = new char[contentLength];
                int totalRead = 0;

                while (totalRead < contentLength) {
                    final int readCount = br.read(buffer, totalRead, contentLength - totalRead);
                    if (readCount == -1) {
                        break;
                    }
                    totalRead += readCount;
                }
                final String body = new String(buffer);
                requestBody = RequestBody.parse(body);
            }
        }

        return new HttpRequest(requestLine, headers, requestBody);
    }
}
