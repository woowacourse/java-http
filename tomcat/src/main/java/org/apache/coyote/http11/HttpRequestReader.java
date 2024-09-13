package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.coyote.http.Cookie;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.RequestBody;
import org.apache.coyote.http.request.RequestHeaders;
import org.apache.coyote.http.request.RequestLine;
import org.apache.coyote.http.request.RequestParameters;

public class HttpRequestReader {

    private static final String START_LINE_DELIMITER = " ";
    private static final String QUERY_STRING_SEPARATOR = "?";

    private static final int HTTP_METHOD_INDEX = 0;
    private static final int REQUEST_URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;

    public static HttpRequest accept(BufferedReader reader) throws IOException {
        String firstLine = reader.readLine();
        RequestLine requestLine = parseRequestLine(firstLine);
        RequestHeaders requestHeaders = readHeaders(reader);
        RequestParameters requestParameters = parseParameters(firstLine);
        Cookie cookie = requestHeaders.parseCookie();
        RequestBody requestBody = readRequestBody(reader, requestHeaders.getContentLength());
        return new HttpRequest(requestLine, requestHeaders, requestParameters, cookie, requestBody);
    }

    private static RequestLine parseRequestLine(String line) {
        String[] token = line.split(START_LINE_DELIMITER);
        HttpMethod httpMethod = HttpMethod.from(token[HTTP_METHOD_INDEX]);
        String uri = parseUri(token[REQUEST_URI_INDEX]);
        String protocol = token[HTTP_VERSION_INDEX];
        return new RequestLine(httpMethod, uri, protocol);
    }

    private static String parseUri(String path) {
        if (path.contains(QUERY_STRING_SEPARATOR)) {
            int endIndex = path.indexOf(QUERY_STRING_SEPARATOR);
            return path.substring(0, endIndex);
        }
        return path;
    }

    private static RequestHeaders readHeaders(BufferedReader reader) {
        String[] headers = reader.lines()
                .takeWhile(line -> !line.isEmpty())
                .toArray(String[]::new);
        return new RequestHeaders(headers);
    }

    private static RequestParameters parseParameters(String requestLine) {
        String path = requestLine.split(START_LINE_DELIMITER)[REQUEST_URI_INDEX];
        if (!path.contains(QUERY_STRING_SEPARATOR)) {
            return RequestParameters.empty();
        }
        int beginIndex = path.indexOf(QUERY_STRING_SEPARATOR) + 1;
        String parameters = path.substring(beginIndex);
        return new RequestParameters(parameters);
    }

    private static RequestBody readRequestBody(BufferedReader reader, int contentLength) throws IOException {
        if (contentLength == 0) {
            return RequestBody.empty();
        }
        char[] buffer = new char[contentLength];
        int ignore = reader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);
        return RequestBody.fromFormData(requestBody);
    }
}
