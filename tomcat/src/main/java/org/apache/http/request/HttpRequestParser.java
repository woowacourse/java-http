package org.apache.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.http.HttpHeader;

public class HttpRequestParser {

    private HttpRequestParser() {
    }

    public static HttpRequest parseRequest(final BufferedReader bufferedReader) throws IOException {
        final String request = bufferedReader.readLine();

        final String[] requestStartLine = request.split(" ");
        final String method = requestStartLine[0];
        final String path = requestStartLine[1];
        final String version = requestStartLine[2];

        final HttpHeader[] headers = parseRequestHeaders(bufferedReader);
        return new HttpRequest(method, path, version, headers, parseRequestBody(headers, bufferedReader));
    }

    private static HttpHeader[] parseRequestHeaders(final BufferedReader bufferedReader) throws IOException {
        String header = bufferedReader.readLine();

        final List<HttpHeader> headers = new ArrayList<>();
        while (header != null && !header.isEmpty()) {
            String[] keyAndValue = header.split(": ");
            headers.add(new HttpHeader(keyAndValue[0], keyAndValue[1]));
            header = bufferedReader.readLine();
            if(header.isEmpty()) {
                break;
            }
        }

        return headers.toArray(HttpHeader[]::new);
    }

    private static String parseRequestBody(final HttpHeader[] headers, final BufferedReader bufferedReader) throws IOException {
        Optional<HttpHeader> httpHeader = Arrays.stream(headers)
                .filter(header -> header.getKey().equalsIgnoreCase("Content-Length"))
                .findFirst();

        if (httpHeader.isEmpty()) {
            return null;
        }

        int contentLength = Integer.parseInt(httpHeader.get().getValue());
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }
}
