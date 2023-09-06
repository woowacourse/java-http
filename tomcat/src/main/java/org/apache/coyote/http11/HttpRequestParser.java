package org.apache.coyote.http11;

import org.apache.coyote.http.request.HttpBody;
import org.apache.coyote.http.request.HttpHeader;
import org.apache.coyote.http.request.HttpHeaders;
import org.apache.coyote.http.request.HttpMethod;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.Protocol;
import org.apache.coyote.http.request.RequestLine;
import org.apache.coyote.http.request.RequestUri;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.apache.coyote.http.request.HttpHeader.CONTENT_LENGTH;
import static org.apache.coyote.http.request.HttpMethod.POST;

public class HttpRequestParser {

    public static final String SPACE = " ";

    public HttpRequestParser() {
    }

    public HttpRequest parseHttpRequest(final BufferedReader bufferedReader) throws IOException {
        final String firstLine = bufferedReader.readLine();
        if (firstLine == null) {
            return null;
        }

        final RequestLine requestLine = parseStartLine(firstLine);
        final HttpHeaders headers = parseHeader(bufferedReader);
        final HttpBody requestBody = parseRequestBody(requestLine.getHttpMethod(), headers, bufferedReader);

        return new HttpRequest(requestLine, headers, requestBody);
    }

    private RequestLine parseStartLine(final String startLine) {
        final String[] parsedStartLine = startLine.split(SPACE);
        final HttpMethod httpMethod = HttpMethod.from(parsedStartLine[0]);
        final RequestUri requestUri = new RequestUri(parsedStartLine[1]);
        final Protocol httpProtocol = Protocol.from(parsedStartLine[2]);

        return new RequestLine(httpMethod, requestUri, httpProtocol);
    }

    private HttpHeaders parseHeader(final BufferedReader bufferedReader) throws IOException {
        final Map<HttpHeader, String> headers = new HashMap<>();
        String header = bufferedReader.readLine();
        while (!"".equals(header)) {
            final String[] parsedHeader = header.split(": ");
            headers.put(HttpHeader.from(parsedHeader[0]), parsedHeader[1]);
            header = bufferedReader.readLine();
        }

        return new HttpHeaders(headers);
    }

    private HttpBody parseRequestBody(final HttpMethod httpMethod, final HttpHeaders headers, final BufferedReader bufferedReader) throws IOException {
        if (httpMethod == POST) {
            final int contentLength = Integer.parseInt(headers.get(CONTENT_LENGTH));
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            return new HttpBody(new String(buffer));
        }

        return HttpBody.empty();
    }
}
