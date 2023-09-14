package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpCookie;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private static final String CONTENT_LENGTH = "Content-Length";

    private final RequestLine requestLine;
    private final RequestHeader requestHeader;
    private final String requestBody;

    private HttpRequest(final RequestLine requestLine, final RequestHeader requestHeader, final String requestBody) {
        this.requestLine = requestLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final BufferedReader bufferedReader) throws IOException {
        final RequestLine requestLine = readRequestLine(bufferedReader);
        final RequestHeader requestHeader = readRequestHeader(bufferedReader);
        final String requestBody = readRequestBody(bufferedReader, requestHeader);
        return new HttpRequest(requestLine, requestHeader, requestBody);
    }

    private static RequestLine readRequestLine(final BufferedReader bufferedReader) throws IOException {
        final String line = bufferedReader.readLine();
        if (line == null) {
            return null;
        }
        return RequestLine.from(line);
    }

    private static RequestHeader readRequestHeader(final BufferedReader bufferedReader) throws IOException {
        final var lines = new ArrayList<String>();
        String line;
        while (!"".equals(line = bufferedReader.readLine())) {
            lines.add(line);
        }
        return RequestHeader.from(lines);
    }

    private static String readRequestBody(final BufferedReader bufferedReader, final RequestHeader requestHeader)
            throws IOException {
        if (requestHeader.isNotFormContentType()) {
            return null;
        }
        final var contentLength = Integer.parseInt(requestHeader.getValue(CONTENT_LENGTH));
        final var buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public Map<String, String> getRequestParameters() {
        final String[] uri = requestLine.getPath().split("\\?");
        if (requestBody == null) {
            return parseRequestBody(uri[1]);
        }
        return parseRequestBody(requestBody);
    }

    private Map<String, String> parseRequestBody(final String requestBody) {
        final var requestBodyValues = new HashMap<String, String>();
        final String[] splitRequestBody = requestBody.split("&");
        for (var value : splitRequestBody) {
            final String[] splitValue = value.split("=");
            requestBodyValues.put(splitValue[0], splitValue[1]);
        }
        return requestBodyValues;
    }

    public HttpCookie getCookie() {
        final String cookieHeader = requestHeader.getValue("Cookie");
        return HttpCookie.from(cookieHeader);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeader getRequestHeader() {
        return requestHeader;
    }

    public String getRequestBody() {
        return requestBody;
    }
}
