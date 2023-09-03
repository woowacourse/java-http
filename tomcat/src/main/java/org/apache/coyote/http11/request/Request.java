package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.coyote.http11.common.Connection;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.Method;
import org.apache.coyote.http11.util.UriComponentsBuilder;

public class Request {

    private final Method method;
    private final String uri;
    private final String host;
    private final List<String> accept;
    private final Connection connection;
    private final String body;

    private Request(
            Method method,
            String uri,
            String host,
            List<String> accept,
            Connection connection,
            String body
    ) {
        this.method = method;
        this.uri = uri;
        this.host = host;
        this.accept = new ArrayList<>(accept);
        this.connection = connection;
        this.body = body;
    }

    public static Request from(
            String methodName,
            String requestURI,
            String host,
            List<String> accept,
            String connectionName,
            String body
    ) {
        Method method = Method.find(methodName)
                .orElseThrow(() -> new IllegalArgumentException("invalid method"));
        Connection connection = Connection.find(connectionName)
                .orElse(null);

        return new Request(method, requestURI, host, accept, connection, body);
    }

    public static Optional<Request> read(BufferedReader bufferedReader) throws IOException {
        String requestHead = bufferedReader.readLine();

        Map<String, String> headers = extractHeaders(bufferedReader);
        if (headers.isEmpty()) {
            return Optional.empty();
        }

        String[] head = requestHead.split(" ");
        String method = head[0];
        String uri = head[1];
        String host = headers.get("Host");
        String accepts = headers.get("Accept");
        String connection = headers.get("Connection");
        String body = readBody(bufferedReader, headers);

        return Optional.of(
                Request.from(
                        method,
                        uri,
                        host,
                        parseAccept(accepts),
                        connection,
                        body
                )
        );
    }

    private static String readBody(BufferedReader bufferedReader, Map<String, String> headers) throws IOException {
        String body = "";
        if (headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            char[] bodyChars = new char[contentLength];
            bufferedReader.read(bodyChars, 0, contentLength);
            body = new String(bodyChars);
        }
        return body;
    }

    private static List<String> parseAccept(String accepts) {
        if (Objects.isNull(accepts)) {
            return new ArrayList<>();
        }
        return Arrays.stream(accepts.split(","))
                .filter(accept -> ContentType.from(accept).isPresent())
                .collect(Collectors.toList());
    }

    private static Map<String, String> extractHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while (!"".equals((line = bufferedReader.readLine()))) {
            String[] header = line.split(": ");
            String key = header[0];
            String value = header[1].trim();
            headers.put(key, value);
        }
        return headers;
    }

    public Map<String, List<String>> getQueryParams() {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.of(uri);

        return uriComponentsBuilder.build().getQueryParams();
    }

    public String getPath() {

        return URI.create(uri).getPath();
    }

    public Method getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Request{" +
                "method=" + method +
                ", URI='" + uri + '\'' +
                ", host='" + host + '\'' +
                ", accept=" + accept +
                ", connection=" + connection +
                ", body='" + body + '\'' +
                '}';
    }

    // TODO Builder 패턴 적용
}
