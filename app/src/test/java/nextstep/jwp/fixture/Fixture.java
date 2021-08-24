package nextstep.jwp.fixture;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Fixture {
    private static final String lineSeparator = "\r\n";

    public static String getHttpRequest() {
        return httpRequest("GET");
    }

    public static String postHttpRequest(String body) {
        return httpRequest("POST", body);
    }

    public static String putHttpRequest(String body) {
        return httpRequest("PUT", body);
    }

    public static String deleteHttpRequest() {
        return httpRequest("DELETE");
    }

    private static String httpRequest(String httpMethod, String body) {
        return httpRequest(httpMethod) + lineSeparator.repeat(2) + body;
    }

    private static String httpRequest(String httpMethod) {
        Map<String, String> fixtureHeaders = getFixtureHeaders();
        String headers = fixtureHeaders.entrySet().stream()
            .map(entry -> String.format("%s: %s", entry.getKey(), entry.getValue()))
            .collect(Collectors.joining(lineSeparator));

        List<String> httpRequestPieces = List.of(
            String.format("%%s %s HTTP/1.1", getResourcePath()),
            headers
        );

        return String.format(String.join(lineSeparator, httpRequestPieces), httpMethod);
    }

    public static Map<String, String> getFixtureHeaders() {
        Map<String, String> headers = new HashMap<>();

        headers.put("Host", "localhost:8080");
        headers.put("Connection", "keep-alive");
        headers.put("Accept", "*/*");

        return headers;
    }

    public static String getResourcePath() {
        return "/nextstep.txt";
    }
}