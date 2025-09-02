package org.apache.coyote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record HttpRequest(
        HttpMethod method,
        String uri
) {
    public static HttpRequest from(InputStream inputStream) throws IOException {
        List<String> strings = readInputStream(inputStream);
        String[] headers = strings.getFirst().split(" ");
        String method = headers[0];
        String uri = headers[1];
        return new HttpRequest(HttpMethod.parse(method), uri);
    }

    private static List<String> readInputStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        List<String> result = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            result.add(line);
            if (line.isEmpty()) {
                break;
            }
        }

        return result;
    }

    public enum HttpMethod {
        GET,
        POST,
        PUT,
        PATCH,
        DELETE,
        OPTIONS,
        ;

        public static HttpMethod parse(String str) {
            return Arrays.stream(HttpMethod.values())
                    .filter(value -> value.name().equalsIgnoreCase(str))
                    .findFirst()
                    .orElseThrow();
            // TODO : 명확한 예외 타입 사용
        }
    }
}
