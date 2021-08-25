package nextstep.jwp.fixture;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.jwp.http.header.element.Body;
import nextstep.jwp.http.header.element.Headers;
import nextstep.jwp.http.header.request.HttpRequest;
import nextstep.jwp.http.header.request.request_line.HttpMethod;
import nextstep.jwp.http.header.request.request_line.RequestLine;

public class Fixture {
    private static final String lineSeparator = "\r\n";

    public static String getHttpRequest() {
        return httpRequest("GET");
    }

    public static String rawGetHttpRequest(String path) {
        return httpRequest("GET", path);
    }

    public static String rawPostHttpRequest(String body) {
        return httpRequestWithBody("POST", body);
    }

    public static String rawPostHttpRequest(String path, String body) {
        return httpRequestWithBody("POST", path, body);
    }

    public static String rawPutHttpRequest(String body) {
        return httpRequestWithBody("PUT", body);
    }

    public static String rawDeleteHttpRequest() {
        return httpRequest("DELETE");
    }

    private static String httpRequestWithBody(String httpMethod, String body) {
        return httpRequest(httpMethod) + lineSeparator.repeat(2) + body;
    }

    private static String httpRequestWithBody(String httpMethod, String path, String body) {
        return httpRequest(httpMethod, path) + lineSeparator.repeat(2) + body;
    }

    private static String httpRequest(String httpMethod) {
        return httpRequest(httpMethod, getResourcePath());
    }

    private static String httpRequest(String httpMethod, String path) {
        Map<String, String> fixtureHeaders = getFixtureHeaders();
        String headers = fixtureHeaders.entrySet().stream()
            .map(entry -> String.format("%s: %s", entry.getKey(), entry.getValue()))
            .collect(Collectors.joining(lineSeparator));

        List<String> httpRequestPieces = List.of(
            String.format("%%s %s HTTP/1.1", path),
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

    public static String createRequestLine(HttpMethod httpMethod, String path) {
        return String.format("%s %s HTTP/1.1", httpMethod.name(), path);
    }

    public static HttpRequest getHttpRequest(String path) {
        RequestLine requestLine = new RequestLine(createRequestLine(HttpMethod.GET, path));
        Headers headers = new Headers();

        headers.putHeader("Host", "localhost:8080");
        headers.putHeader("Connection", "keep-alive");
        headers.putHeader("Accept", "*/*");

        return new HttpRequest(requestLine, headers, Body.empty());
    }

    public static HttpRequest postHttpRequest(String path, String rawBody) {
        RequestLine requestLine = new RequestLine(createRequestLine(HttpMethod.POST, path));
        Headers headers = new Headers();
        Body body = new Body(rawBody);

        headers.putHeader("Host", "localhost:8080");
        headers.putHeader("Connection", "keep-alive");
        headers.putHeader("Accept", "*/*");
        headers.putHeader("Content-Length", String.valueOf(body.length()));

        return new HttpRequest(requestLine, headers, body);
    }

}
