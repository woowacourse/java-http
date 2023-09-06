package org.apache.coyote.http.util;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.request.HttpRequestBody;
import org.apache.coyote.http.request.HttpRequestHeaders;
import org.apache.coyote.http.request.QueryParameters;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.request.Url;
import org.apache.coyote.http.util.exception.InvalidRequestBodyException;

public class RequestGenerator {

    private static final int START_LINE_METHOD_INDEX = 0;
    private static final int START_LINE_URL_INDEX = 1;
    private static final int START_LINE_HTTP_VERSION = 2;
    private static final int INVALID_READ_COUNT = -1;

    public Request generate(final BufferedReader bufferedReader) throws IOException {
        final String[] startLineTokens = bufferedReader.readLine()
                                                       .split(HttpConsts.SPACE);
        final HttpMethod httpMethod = HttpMethod.findMethod(startLineTokens[START_LINE_METHOD_INDEX]);
        final Url url = Url.from(startLineTokens[START_LINE_URL_INDEX]);
        final HttpVersion httpVersion = HttpVersion.findVersion(startLineTokens[START_LINE_HTTP_VERSION]);
        final HttpRequestHeaders headers = readRequestHeaders(bufferedReader);
        final HttpRequestBody body = readRequestBody(bufferedReader, headers);
        final QueryParameters queryParameters = readQueryParameters(url, body);
        final HttpCookie cookie = convertCookie(headers);

        return new Request(headers, httpMethod, httpVersion, url, body, queryParameters, cookie);
    }

    private HttpRequestHeaders readRequestHeaders(final BufferedReader bufferedReader) throws IOException {
        final StringBuilder requestHeaderBuilder = new StringBuilder();

        for (String line = bufferedReader.readLine(); !HttpConsts.BLANK.equals(line); line = bufferedReader.readLine()) {
            requestHeaderBuilder.append(line)
                                .append(HttpConsts.CRLF);
        }

        return HttpRequestHeaders.from(requestHeaderBuilder.toString());
    }

    private HttpRequestBody readRequestBody(
            final BufferedReader bufferedReader,
            final HttpRequestHeaders headers
    ) throws IOException {
        if (headers.isRequestBodyEmpty()) {
            return HttpRequestBody.EMPTY;
        }

        final int contentLength = Integer.parseInt(headers.findValue(HttpHeaderConsts.CONTENT_LENGTH));

        if (contentLength == 0) {
            return HttpRequestBody.EMPTY;
        }

        char[] bodyBuffer = new char[contentLength];
        final int result = bufferedReader.read(bodyBuffer, 0, contentLength);

        if (result == INVALID_READ_COUNT) {
            throw new InvalidRequestBodyException();
        }

        final String bodyContent = new String(bodyBuffer);

        return new HttpRequestBody(bodyContent);
    }

    private QueryParameters readQueryParameters(final Url url, final HttpRequestBody body) {
        final String content = url.url();
        final QueryParameters queryParameters = QueryParameters.fromUrlContent(content);

        if (queryParameters != QueryParameters.EMPTY || body.isEmpty()) {
            return queryParameters;
        }

        return QueryParameters.fromBodyContent(body.body());
    }

    private HttpCookie convertCookie(final HttpRequestHeaders headers) {
        final String cookieValue = headers.findValue(HttpHeaderConsts.COOKIE);

        if (cookieValue == null) {
            return HttpCookie.EMPTY;
        }

        return HttpCookie.fromCookieHeaderValue(cookieValue);
    }
}
