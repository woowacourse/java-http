package org.apache.coyote.http11.data;

import static org.apache.coyote.http11.data.SessionManager.JSESSIONID_COOKIE_NAME;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.config.TomcatServerConfiguration;

public class Request {
    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final Map<String, String> queryParameters;
    private final Map<String, String> bodyParameters;
    private final Cookies cookies;
    private Session session;

    private Request(
            final RequestLine requestLine,
            final Map<String, String> headers,
            final Map<String, String> queryParameters,
            final Map<String, String> bodyParameters,
            final Cookies cookies
    ) {
        this.requestLine = requestLine;
        this.headers = Map.copyOf(headers);
        this.queryParameters = Map.copyOf(queryParameters);
        this.bodyParameters = Map.copyOf(bodyParameters);
        this.cookies = cookies;
    }

    public static Request from(final InputStream inputStream) throws IOException {
        final RequestLine requestEndPoint = requestEndPoint(inputStream);
        final Map<String, String> queryParameters = parseQueryParameter(requestEndPoint.getQuery());
        final Map<String, String> headers = readHeader(inputStream);
        final Map<String, String> body = readBody(inputStream, Integer.parseInt(headers.getOrDefault("Content-Length", "0")));
        final Cookies cookies = Cookies.fromHeaderValue(headers.getOrDefault("Cookie", ""));

        return new Request(requestEndPoint, headers, queryParameters, body, cookies);
    }

    private static RequestLine requestEndPoint(final InputStream inputStream) throws IOException {
        final String requestLine = readLine(inputStream);

        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("HTTP 요청 라인이 전달되지 않았습니다.");
        }

        return RequestLine.from(requestLine);
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

    private static Map<String, String> readBody(
            final InputStream inputStream,
            final int contentLength
    ) throws IOException {

        if (contentLength == 0) {
            return Map.of();
        }

        final byte[] bodyBytes = inputStream.readNBytes(contentLength);

        if (bodyBytes.length != contentLength) {
            throw new IOException("Request body를 모두 읽지 못했습니다.");
        }

        final String body = new String(bodyBytes, TomcatServerConfiguration.DEFAULT_CHARSET).trim();
        final String[] bodySplit = body.split("&");

        final Map<String, String> bodyMap = new HashMap<>();
        for (String data : bodySplit) {
            final String[] keyValue = data.split("=");

            if (keyValue.length != 2) {
                throw new IOException("잘못된 HTTP 요청 바디 형식입니다: " + body);
            }

            bodyMap.put(keyValue[0], URLDecoder.decode(keyValue[1], TomcatServerConfiguration.DEFAULT_CHARSET));
        }

        return bodyMap;
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

    public RequestLine getRequestPoint() {
        return requestLine;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    public Map<String, String> getBody() {
        return bodyParameters;
    }

    public Cookies getCookies() {
        return cookies;
    }

    public Session getSession(boolean create) {
        if (!create) {
            return session;
        }
        session = cookies.getValue(JSESSIONID_COOKIE_NAME)
                .flatMap(SessionManager::getSession)
                .orElseGet(SessionManager::createSession);
        return session;
    }

    public Session getSession() {
        return getSession(true);
    }

    public static class RequestLine {
        private final String method;
        private final String path;
        private final String query;
        private final String version;

        private RequestLine(String method, String path, String query, String version) {
            this.method = method;
            this.path = path;
            this.query = query;
            this.version = version;
        }

        private static RequestLine from(final String requestLine) {
            final String[] requestLineParts = requestLine.split(" ");
            final String[] split = requestLineParts[1].split("\\?");
            final String path = split[0];
            final String query = split.length > 1 ? split[1] : "";

            return new RequestLine(
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
