package org.apache.coyote.http11;

import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.http11.cookie.Cookies;
import org.apache.coyote.http11.header.Headers;
import org.apache.coyote.http11.header.RequestHeader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpRequestParser {
    public static HttpRequest parse(final InputStream is) throws IOException {
        final var inputReader = new BufferedReader(new InputStreamReader(is));
        final RequestLine requestLine = createRequestLine(inputReader);
        final Headers headers = createHeaders(inputReader);
        final RequestBody requestBody = createBody(inputReader, headers.get(RequestHeader.CONTENT_LENGTH), headers.get(RequestHeader.CONTENT_TYPE));
        final Cookies cookies = createCookies(headers.get(RequestHeader.COOKIE));

        return new HttpRequest(requestLine, headers, requestBody, cookies);
    }

    private static RequestLine createRequestLine(final BufferedReader reader) throws IOException {
        return RequestLine.create(reader.readLine());
    }

    private static Headers createHeaders(final BufferedReader reader) throws IOException {
        final Headers headers = new Headers();
        String line;
        while ((line = reader.readLine()) != null && !StringUtils.isEmpty(line)) {
            headers.put(line);
        }
        return headers;
    }

    private static RequestBody createBody(final BufferedReader reader, final String contentLength, final String contentType) throws IOException {
        if (StringUtils.isEmpty(contentLength)) {
            return RequestBodyCreator.EMPTY_BODY;
        }

        final int length = Integer.parseInt(contentLength);
        final char[] buffer = new char[length];
        final int bytesRead = reader.read(buffer, 0, length);

        return RequestBodyCreator.create(contentType, new String(buffer, 0, bytesRead));
    }

    private static Cookies createCookies(final String cookies) {
        return Cookies.from(cookies);
    }

    private HttpRequestParser() {}
}
