package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.header.HttpHeaderName.CONTENT_LENGTH;
import static org.apache.coyote.http11.header.HttpHeaderName.COOKIE;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpMethod;

public class HttpRequest {
    public static final String HEADER_DELIMITER = ": ";
    public static final String QUERY_STRING_DELIMITER = "&";
    public static final String QUERY_TOKEN_DELIMITER = "=";
    public static final int NAME_INDEX = 0;
    public static final int VALUE_INDEX = 1;
    public static final String EMPTY_BODY_LENGTH = "0";

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final String requestBody;

    private HttpRequest(final RequestLine requestLine, final Map<String, String> headers, final String requestBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final BufferedReader bufferedReader) throws IOException {
        final RequestLine requestLine = parseRequestLine(bufferedReader);
        final Map<String, String> headers = parseHeaders(bufferedReader);
        final int contentLength = Integer.parseInt(headers.getOrDefault(CONTENT_LENGTH.getName(), EMPTY_BODY_LENGTH));
        final String body = parseBody(bufferedReader, contentLength);
        return new HttpRequest(requestLine, headers, body);
    }

    private static RequestLine parseRequestLine(final BufferedReader reader) throws IOException {
        return RequestLine.from(reader.readLine());
    }

    private static Map<String, String> parseHeaders(final BufferedReader reader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String header = reader.readLine();

        while (!"".equals(header)) {
            final String[] tokens = header.split(HEADER_DELIMITER);
            headers.put(tokens[NAME_INDEX], tokens[VALUE_INDEX]);
            header = reader.readLine();
        }

        return headers;
    }

    private static String parseBody(final BufferedReader reader, final int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public HttpMethod getRequestMethod() {
        return requestLine.getMethod();
    }

    public String getRequestPath() {
        return requestLine.getPath();
    }

    public HttpCookie getCookies() {
        final String cookieString = headers.getOrDefault(COOKIE.getName(), "");
        return HttpCookie.from(cookieString);
    }

    public Map<String, String> parseRequestQuery() {
        if ("".equals(requestBody)) {
            return new HashMap<>();
        }
        final String[] params = requestBody.split(QUERY_STRING_DELIMITER);

        return Arrays.stream(params)
                .map(param -> param.split(QUERY_TOKEN_DELIMITER))
                .collect(Collectors.toMap(token -> token[NAME_INDEX], token -> token[VALUE_INDEX]));
    }
}
