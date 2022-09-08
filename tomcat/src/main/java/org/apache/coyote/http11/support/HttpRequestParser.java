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
            String[] split = line.split(":");
            headers.put(split[0], split[1].trim());
        }
        return new HttpHeaders(headers);
    }

    private static RequestBody extractBody(BufferedReader bufferedReader, HttpHeaders httpHeaders) throws IOException {
        RequestBody requestBody = new RequestBody();
        if (httpHeaders.containsKey("Content-Length")) {
            requestBody = extractRequestBody(Integer.parseInt(httpHeaders.get("Content-Length")), bufferedReader);
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
        if (httpHeaders.containsKey("Cookie")) {
            String cookieValue = httpHeaders.get("Cookie");
            Map<String, String> cookieValueMap = Arrays.stream(cookieValue.split("; "))
                    .map(it -> it.split("="))
                    .collect(Collectors.toMap(it -> it[0], it -> it[1]));

            httpCookie.putAll(cookieValueMap);
        }
        return httpCookie;
    }
}
