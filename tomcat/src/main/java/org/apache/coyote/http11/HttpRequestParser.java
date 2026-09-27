package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HttpRequestParser {

    public HttpRequest parse(BufferedReader bufferedReader) throws IOException {
        final String line = getHttpRequestLine(bufferedReader);
        final RequestLine requestLine = new RequestLine(line);
        final Map<String, String> headers = parserHeader(bufferedReader);
        final String parserBody = parserBody(headers, bufferedReader);

        final Map<String, String > params = new HashMap<>(requestLine.getParams());

        if (isFormUrlEncoded(headers)) {
            params.putAll(getParamsMap(parserBody));
        }

        return HttpRequest.builder()
                .httpMethod(requestLine.getHttpMethod())
                .path(requestLine.getPath())
                .version(requestLine.getVersion())
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

    private Map<String, String> getParamsMap(String str) {
        Map<String, String> paramsMap = new HashMap<>();
        String[] data = str.split("\\&");
        for (String d : data) {
            String[] param = d.split("\\=");
            String key = URLDecoder.decode(param[0], StandardCharsets.UTF_8);
            String value = URLDecoder.decode(param[1], StandardCharsets.UTF_8);

            paramsMap.put(key, value);
        }
        return paramsMap;
    }
}
