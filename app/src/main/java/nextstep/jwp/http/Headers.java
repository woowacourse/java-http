package nextstep.jwp.http;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static nextstep.jwp.http.Protocol.LINE_SEPARATOR;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class Headers {

    private final Map<String, String> headers;

    public Headers(String httpRequest) {
        this.headers = extractHeaders(httpRequest);
    }

    public Headers() {
        this(new HashMap<>());
    }

    public Headers(Map<String, String> headers) {
        this.headers = headers;
    }

    private Map<String, String> extractHeaders(String rawHeaders) {
        return rawHeadersToMap(List.of(rawHeaders.split(LINE_SEPARATOR)));
    }

    private Map<String, String> rawHeadersToMap(List<String> rawHeaders) {
        return rawHeaders.stream()
            .filter(header -> !header.isBlank())
            .map(rawHeader -> rawHeader.split(" "))
            .collect(toMap(toHeaderName(), toHeaderValue()));
    }

    private Function<String[], String> toHeaderName() {
        return parameters -> parameters[0].replaceAll(":", "").trim();
    }

    private Function<String[], String> toHeaderValue() {
        return parameters -> parameters[1].trim();
    }

    public Optional<String> getHeader(String header) {
        return Optional.ofNullable(headers.get(header));
    }

    public void putHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public String asString() {
        return headers.entrySet().stream()
            .map(entry -> String.format("%s: %s ", entry.getKey(), entry.getValue()))
            .collect(joining(LINE_SEPARATOR));
    }
}
