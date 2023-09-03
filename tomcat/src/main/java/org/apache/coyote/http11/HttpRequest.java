package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequest {

    private static final String CRLF = "\r\n";
    private static final String WHITE_SPACE = " ";

    private final String method;
    private final String target;
    private final String version;
    private final List<HttpHeader> headers;
    private final Map<String, String> queries;
    private final String body;

    private HttpRequest(final String method, final String target, final String version, final List<HttpHeader> headers,
                        final Map<String, String> queries,
                        final String body) {
        this.method = method;
        this.target = target;
        this.version = version;
        this.headers = headers;
        this.queries = queries;
        this.body = body;
    }

    public static HttpRequest from(InputStream inputStream) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String[] requestLineElements = reader.readLine().split(WHITE_SPACE);

            List<String> headerLines = new ArrayList<>();
            String next;
            while (reader.ready() && !(next = reader.readLine()).isEmpty()) {
                headerLines.add(next);
            }

            List<String> bodyLines = new ArrayList<>();
            while (reader.ready()) {
                next = reader.readLine();
                bodyLines.add(next);
            }
            String body = String.join(CRLF, bodyLines);

            return new HttpRequest(
                    requestLineElements[0],
                    removeQueriesFrom(requestLineElements[1]),
                    requestLineElements[2],
                    headerLines.stream()
                            .map(HttpHeader::of)
                            .collect(Collectors.toList()),
                    getQueriesFrom(requestLineElements[1]),
                    body
            );
        } catch (IOException e) {
            throw new IllegalArgumentException("HTTP 요청을 잘 읽지 못했습니다");
        }
    }

    private static Map<String, String> getQueriesFrom(String target) {
        int queryStringStart = target.indexOf('?');
        if (queryStringStart == -1) {
            return Collections.emptyMap();
        }
        String rawQueryString = target.substring(queryStringStart + 1);
        String[] rawQueries = rawQueryString.split("&");
        return Arrays.stream(rawQueries)
                .collect(Collectors.toMap(
                        rawQuery -> rawQuery.split("=")[0],
                        rawQuery -> rawQuery.split("=")[1]
                ));
    }

    private static String removeQueriesFrom(String target) {
        int queryStringStart = target.indexOf('?');
        if (queryStringStart == -1) {
            return target;
        }
        return target.substring(0, queryStringStart);
    }

    public String getMethod() {
        return method;
    }

    public String getTarget() {
        return target;
    }

    public String getVersion() {
        return version;
    }

    public List<HttpHeader> getHeaders() {
        return headers;
    }

    public HttpHeader getHeader(String name) {
        return headers.stream()
                .filter(httpHeader -> httpHeader.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public String getQuery(String name) {
        return queries.getOrDefault(name, "");
    }

    public String getBody() {
        return body;
    }
}
