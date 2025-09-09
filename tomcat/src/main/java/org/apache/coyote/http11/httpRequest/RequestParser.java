package org.apache.coyote.http11.httpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RequestParser {

    public static HttpRequest parse(final BufferedReader br) throws IOException {
        final RequestLine requestLine = RequestLine.parse(br.readLine());

        final RequestHeader requestHeader = getRequestHeader(br);
        final RequestBody requestBody = getRequestBody(br, requestLine, requestHeader);

        return new HttpRequest(requestLine, requestHeader, requestBody);
    }

    private static RequestHeader getRequestHeader(final BufferedReader br) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = br.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            final String[] headerTokens = line.split(": ");
            headers.put(headerTokens[0], headerTokens[1]);
        }
        return new RequestHeader(headers);
    }

    private static RequestBody getRequestBody(
            final BufferedReader br,
            final RequestLine requestLine,
            final RequestHeader headers
    ) throws IOException {
        RequestBody requestBody = RequestBody.empty();
        final Optional<String> contentLengthOrEmpty = headers.findValue("Content-Length");
        if (requestLine.getRequestMethod() == RequestMethod.POST && contentLengthOrEmpty.isPresent()) {
            final String length = contentLengthOrEmpty.get();
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
        return requestBody;
    }
}
