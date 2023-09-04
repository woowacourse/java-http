package nextstep.jwp.http;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpHeaders {

    private final Map<String, String> httpHeaders;

    private HttpHeaders(Map<String, String> httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public static HttpHeaders from(List<String> lines) {
        Map<String, String> headers = lines.stream()
                .map(line -> line.split(": "))
                .collect(Collectors.toMap(line -> line[0], line -> line[1]));

        return new HttpHeaders(headers);
    }

    // TODO: 2023/09/04 예외처리 수정
    public String get(String key) {
        if (containsKey(key)) {
            return httpHeaders.get(key);
        }

        throw new IllegalArgumentException();
    }

    public boolean containsKey(String key) {
        return httpHeaders.containsKey(key);
    }

    public String getHeaders() {
        return httpHeaders.keySet()
                .stream()
                .map(key -> key + ": " + httpHeaders.get(key))
                .collect(Collectors.joining("\r\n"));
    }

    public void addHeader(String key, String value) {
        httpHeaders.put(key, value);
    }

}
