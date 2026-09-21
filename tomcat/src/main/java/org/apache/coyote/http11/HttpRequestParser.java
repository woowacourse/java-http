package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class HttpRequestParser {

    public HttpRequest parse(BufferedReader bufferedReader) throws IOException {
        final String line = getHttpRequestLine(bufferedReader);
        final HttpRequestLine requestLine = parseRequestLine(line);
        final Map<String, String> headers = parserHeader(bufferedReader);
        final String parserBody = parserBody(headers, bufferedReader);

        final Map<String, String > params = new HashMap<>(requestLine.params);

        if (isFormUrlEncoded(headers)) {
            params.putAll(getParamsMap(parserBody));
        }

        return HttpRequest.builder()
                .httpMethod(requestLine.httpMethod)
                .path(requestLine.path)
                .version(requestLine.version)
                .headers(headers)
                .params(params)
                .body(parserBody)
                .build();
    }

    private String getHttpRequestLine(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        if (line == null) {
            throw new IllegalArgumentException("HTTP Request Line은 null일 수 없습니다.");
        }
        return line;
    }

    private HttpRequestLine parseRequestLine(String line) {
        final String[] tokens = line.split(" ", 3);
        final HttpMethod method = HttpMethod.of(tokens[0]);
        final String uri = tokens[1];
        final String version = tokens[2];

        final String path = getPath(uri);
        final Map<String, String> params = getParams(uri);
        return new HttpRequestLine(method, path, version, params);
    }

    private String getPath(String uri) {
        if (uri.contains("?")) {
            int index = uri.indexOf("?");
            return uri.substring(0, index);
        }
        return uri;
    }

    private Map<String, String> getParams(String uri) {
        return getQueryString(uri)
                .map(this::getParamsMap)
                .orElseGet(Collections::emptyMap);
    }

    private Optional<String> getQueryString(String uri) {
        if (uri.contains("?")) {
            int index = uri.indexOf("?");
            return Optional.of(uri.substring(index + 1));
        }
        return Optional.empty();
    }

    private Map<String, String> getParamsMap(String str) {
        Map<String, String> paramsMap = new HashMap<>();
        String[] data = str.split("\\&");
        for (String d : data) {
            String[] param = d.split("\\=");
            paramsMap.put(param[0], param[1]);
        }
        return paramsMap;
    }

    private Map<String, String> parserHeader(BufferedReader bufferedReader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !"".equals(line)) {
            final String[] tokens = line.split(":", 2);

            if (tokens.length != 2) {
                throw new IllegalArgumentException("잘못된 HTTP 헤더입니다: " + line);
            }

            final String name = tokens[0].trim().toLowerCase(Locale.ROOT);
            final String value = tokens[1].trim();

            headers.put(name, value);
        }
        return headers;
    }

    private String parserBody(Map<String, String> headers, BufferedReader bufferedReader) throws IOException {
        final int contentLength = Integer.parseInt(
                headers.getOrDefault("content-length", "0")
        );

        if (contentLength == 0) {
            return "";
        }

        char[] buffer = new char[contentLength];
        int offset = 0;

        while (offset < contentLength) {
            final int readCount = bufferedReader.read(buffer, offset, contentLength - offset);
            if (readCount == -1) {
                throw new IllegalArgumentException("헤더 정보와 실제 content 길이 다름");
            }
            offset += readCount;
        }

        return new String(buffer);
    }

    private boolean isFormUrlEncoded(Map<String, String> headers) {
        return headers
                .getOrDefault("content-type", "")
                .startsWith("application/x-www-form-urlencoded");
    }

    private record HttpRequestLine(
            HttpMethod httpMethod,
            String path,
            String version,
            Map<String, String> params) {
    }
}
