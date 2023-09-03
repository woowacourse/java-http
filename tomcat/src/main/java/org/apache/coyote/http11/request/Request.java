package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.apache.coyote.http11.common.Connection;
import org.apache.coyote.http11.common.Method;

public class Request {

    private final Method method;
    private final String URI;
    private final String host;
    private final String[] accept;
    private final Connection connection;
    private final String body;

    private Request(
            Method method,
            String URI,
            String host,
            String[] accept,
            Connection connection,
            String body
    ) {
        this.method = method;
        this.URI = URI;
        this.host = host;
        this.accept = accept;
        this.connection = connection;
        this.body = body;
    }

    public static Request from(
            String methodName,
            String requestURI,
            String host,
            String[] accept,
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
        /// TODO: 2023/09/03  body도 읽어오기 (bufferedReader에서 로딩 길어지는 문제 해결해야 함)
        String body = "";

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

    private static String[] parseAccept(String accepts) {
        if (Objects.isNull(accepts)) {
            return new String[1];
        }
        return accepts.split(",");
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

    public String getURI() {
        return URI;
    }

    @Override
    public String toString() {
        return "Request{" +
                "method=" + method +
                ", URI='" + URI + '\'' +
                ", host='" + host + '\'' +
                ", accept=" + Arrays.toString(accept) +
                ", connection=" + connection +
                ", body='" + body + '\'' +
                '}';
    }
}
