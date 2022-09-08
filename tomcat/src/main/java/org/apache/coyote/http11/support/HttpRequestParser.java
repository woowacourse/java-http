package org.apache.coyote.http11.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.http.HttpCookie;
import org.apache.coyote.http11.http.HttpHeaders;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.RequestBody;

public class HttpRequestParser {

    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String COOKIE = "Cookie";

    private static final String HTTP_REQUEST_LINE_DELIMITER = ":";
    private static final String COOKIE_DELIMITER = "; ";
    private static final String COOKIE_VALUE_DELIMITER = "=";

    private HttpRequestParser() {
    }

    public static HttpRequest parse(BufferedReader bufferedReader) throws IOException {
        String startLine = bufferedReader.readLine();
        HttpHeaders httpHeaders = extractHeaders(bufferedReader);
        RequestBody requestBody = extractBody(bufferedReader, httpHeaders);
        HttpCookie httpCookie = extractCookie(httpHeaders);

        return HttpRequest.of(startLine, httpHeaders, requestBody, httpCookie);
    }

    private static HttpHeaders extractHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();

        while (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            if (line.isEmpty()) {
                break;
            }
            String[] split = line.split(HTTP_REQUEST_LINE_DELIMITER);
            headers.put(split[0], split[1].trim());
        }
        return new HttpHeaders(headers);
    }

    private static RequestBody extractBody(BufferedReader bufferedReader, HttpHeaders httpHeaders) throws IOException {
        RequestBody requestBody = new RequestBody();
        if (httpHeaders.containsKey(CONTENT_LENGTH)) {
            requestBody = extractRequestBody(Integer.parseInt(httpHeaders.get(CONTENT_LENGTH)), bufferedReader);
        }
        return requestBody;
    }

    private static RequestBody extractRequestBody(int contentLength, BufferedReader bufferedReader) throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String request = new String(buffer);
        return RequestBody.of(request);
    }

    private static HttpCookie extractCookie(HttpHeaders httpHeaders) {
        HttpCookie httpCookie = new HttpCookie();
        if (httpHeaders.containsKey(COOKIE)) {
            String cookieValue = httpHeaders.get(COOKIE);
            Map<String, String> cookieValueMap = Arrays.stream(cookieValue.split(COOKIE_DELIMITER))
                    .map(it -> it.split(COOKIE_VALUE_DELIMITER))
                    .collect(Collectors.toMap(it -> it[0], it -> it[1]));
            httpCookie.putAll(cookieValueMap);
        }
        return httpCookie;
    }
}
