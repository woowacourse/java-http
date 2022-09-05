package org.apache.coyote;

import static org.apache.coyote.support.HttpRequestParser.parseHttpMethod;
import static org.apache.coyote.support.HttpRequestParser.parseQueryString;
import static org.apache.coyote.support.HttpRequestParser.parseUri;
import static org.apache.coyote.support.HttpRequestParser.parseUrl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.coyote.support.HttpRequestParser;

public class HttpRequest {

    private final HttpMethod httpMethod;
    private final String url;
    private final QueryParams queryParams;
    private final Map<String, String> headers;
    private final String body;

    private HttpRequest(final HttpMethod httpMethod, final String url, final QueryParams queryParams,
                        final Map<String, String> headers,
                        final String body) {
        this.httpMethod = httpMethod;
        this.url = url;
        this.queryParams = queryParams;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(final InputStream inputStream) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String startLine = bufferedReader.readLine();
            HttpMethod httpMethod = parseHttpMethod(startLine);
            String uri = parseUri(startLine);
            String url = parseUrl(uri);
            QueryParams queryParams = QueryParams.parseQueryParams(parseQueryString(uri));
            Map<String, String> headers = HttpRequestParser.parseHeaders(readHeaders(bufferedReader));
            int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
            String body = readBody(bufferedReader, contentLength);

            return new HttpRequest(httpMethod, url, queryParams, headers, body);
        }
    }

    public static HttpRequest from(final BufferedReader bufferedReader) throws IOException {
        String startLine = bufferedReader.readLine();
        HttpMethod httpMethod = parseHttpMethod(startLine);
        String uri = parseUri(startLine);
        String url = parseUrl(uri);
        QueryParams queryParams = QueryParams.parseQueryParams(parseQueryString(uri));
        Map<String, String> headers = HttpRequestParser.parseHeaders(readHeaders(bufferedReader));
        int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
        String body = readBody(bufferedReader, contentLength);

        return new HttpRequest(httpMethod, url, queryParams, headers, body);
    }

    private static List<String> readHeaders(final BufferedReader bufferedReader) throws IOException {
        List<String> headerLines = new ArrayList<>();
        String line;
        while (!(line = bufferedReader.readLine()).isBlank()) {
            headerLines.add(line);
        }
        return headerLines;
    }

    private static String readBody(final BufferedReader bufferedReader, final int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public QueryParams getQueryParams() {
        return queryParams;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
