package org.apache.http.request;

import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpMethod;

public class HttpRequestParser {

    public <T> T parse(Class<T> type, String input) {
        switch (type.getSimpleName()) {
            case "HttpTomcatRequest":
                return type.cast(parseHttpRequest(input));
            default:
                throw new IllegalArgumentException("Unsupported type: " + type.getSimpleName());
        }
    }

    private HttpTomcatRequest parseHttpRequest(String input) {
        String[] parts = input.split("\r\n\r\n", 2);

        String head = parts[0];
        String body = parts.length > 1 ? parts[1] : "";

        String[] lines = head.split("\r\n");

        String[] requestLine = lines[0].split(" ");
        System.out.println("Request Line: " + lines[0]);
        HttpMethod method = HttpMethod.fromString(requestLine[0]);
        String uri = requestLine[1];
        String protocol = requestLine[2];

        Map<String, String> headers = new HashMap<>();

        for (int i = 1; i < lines.length; i++) {
            String[] header = lines[i].split(":", 2);

            if (header.length != 2) {
                continue;
            }

            headers.put(
                    header[0].trim(),
                    header[1].trim()
            );
        }

        return new HttpTomcatRequest(
                method,
                uri,
                protocol,
                headers,
                body
        );
    }
}

