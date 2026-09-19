package org.apache.coyote.http11.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.config.TomcatServerConfiguration;

public class Request {
    private final RequestPoint requestPoint;
    private final Map<String, String> headers;
    private final Map<String, String> queryParameters;
    private final String body;

    private Request(
            final RequestPoint requestPoint,
            final Map<String, String> headers,
            final Map<String, String> queryParameters,
            final String body
    ) {
        this.requestPoint = requestPoint;
        this.headers = headers;
        this.queryParameters = queryParameters;
        this.body = body;
    }

    public static Request from(final InputStream inputStream) throws IOException {
        final RequestPoint requestEndPoint = requestEndPoint(inputStream);
        final Map<String, String> queryParameters = parseQueryParameter(requestEndPoint.getQuery());
        final Map<String, String> headers = readHeader(inputStream);
        final String body = readBody(inputStream, Integer.parseInt(headers.getOrDefault("Content-Length", "0")));

        return new Request(requestEndPoint, headers, queryParameters, body);
    }

    private static RequestPoint requestEndPoint(final InputStream inputStream) throws IOException {
        final String requestLine = readLine(inputStream);

        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("HTTP 요청 라인이 전달되지 않았습니다.");
        }

        return RequestPoint.from(requestLine);
    }

    private static Map<String, String> readHeader(final InputStream inputStream) throws IOException {
        final Map<String, String> headers = new HashMap<>();

        while (true) {
            final String line = readLine(inputStream);

            if (line == null) {
                throw new IOException("HTTP 요청 헤더가 완전히 전달되지 않았습니다.");
            }

            if (line.isEmpty()) {
                break;
            }

            final String[] header = line.split(":", 2);

            if (header.length != 2) {
                throw new IOException("잘못된 HTTP 헤더 형식입니다: " + line);
            }

            headers.put(
                    header[0].trim(),
                    header[1].trim()
            );
        }

        return Map.copyOf(headers);
    }

    private static String readLine(final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream line = new ByteArrayOutputStream();

        int previous = -1;
        int current;

        while ((current = inputStream.read()) != -1) {
            if (previous == '\r' && current == '\n') {
                return line.toString(TomcatServerConfiguration.DEFAULT_CHARSET);
            }

            if (previous != -1) {
                line.write(previous);
            }

            previous = current;
        }

        if (previous != -1) {
            line.write(previous);
        }

        if(line.size() == 0) {
            return null;
        }

        return line.toString(StandardCharsets.ISO_8859_1);
    }

    private static String readBody(
            final InputStream inputStream,
            final int contentLength
    ) throws IOException {

        if (contentLength == 0) {
            return "";
        }

        final byte[] bodyBytes = inputStream.readNBytes(contentLength);

        if (bodyBytes.length != contentLength) {
            throw new IOException("Request body를 모두 읽지 못했습니다.");
        }

        return new String(bodyBytes, TomcatServerConfiguration.DEFAULT_CHARSET);
    }

    private static Map<String, String> parseQueryParameter(final String query) {
        if (query == null || query.isEmpty()) {
            return Map.of();
        }

        final String[] parameters = query.split("&");

        return Arrays.stream(parameters)
                .map(parameter -> parameter.split("="))
                .filter(keyValue -> keyValue.length == 2)
                .collect(
                        HashMap::new,
                        (map, keyValue) -> map.put(keyValue[0], keyValue[1]),
                        HashMap::putAll
                );
    }

    public RequestPoint getRequestPoint() {
        return requestPoint;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    public String getBody() {
        return body;
    }

    public static class RequestPoint {
        private final String method;
        private final String path;
        private final String query;
        private final String version;

        private RequestPoint(String method, String path, String query, String version) {
            this.method = method;
            this.path = path;
            this.query = query;
            this.version = version;
        }

        private static RequestPoint from(final String requestLine) {
            final String[] requestLineParts = requestLine.split(" ");
            final String[] split = requestLineParts[1].split("\\?");
            final String path = split[0];
            final String query = split.length > 1 ? split[1] : "";

            return new RequestPoint(
                    requestLineParts[0],
                    path,
                    query,
                    requestLineParts[2]
            );
        }

        public String getMethod() {
            return method;
        }

        public String getPath() {
            return path;
        }

        public String getQuery() {
            return query;
        }

        public String getVersion() {
            return version;
        }
    }

}
