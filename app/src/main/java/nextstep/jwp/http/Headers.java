package nextstep.jwp.http;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static nextstep.jwp.http.Protocol.LINE_SEPARATOR;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Headers {

    private final Map<String, String> headers;

    public Headers(String httpRequest) {
        this.headers = extractHeaders(httpRequest);
    }

    public Headers(Map<String, String> headers) {
        this.headers = headers;
    }

    private Map<String, String> extractHeaders(String httpRequest) {
        String rawHeaders = extractRawHeaders(httpRequest);

        return stringHeadersToMap(List.of(rawHeaders.split(LINE_SEPARATOR)));
    }

    private String extractRawHeaders(String httpRequest) {
        int headerSignatureOffset = httpRequest.indexOf(LINE_SEPARATOR);
        int bodySignatureOffset = httpRequest.indexOf(LINE_SEPARATOR.repeat(2));

        if (bodySignatureOffset == -1) {
            return httpRequest.substring(headerSignatureOffset);
        }

        return httpRequest.substring(headerSignatureOffset, bodySignatureOffset);
    }

    private Map<String, String> stringHeadersToMap(List<String> rawHeaders) {
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

    public String asString() {
        return headers.entrySet().stream()
            .map(entry -> String.format("%s: %s", entry.getKey(), entry.getValue()))
            .collect(joining(LINE_SEPARATOR));
    }
}
