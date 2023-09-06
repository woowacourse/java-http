package nextstep.jwp.http;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpHeaders {

    private static final String KET_VALUE_DELIMITER = ": ";
    private static final String HEADER_FORMAT = "%s: %s ";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private final Map<String, String> httpHeaders;

    private HttpHeaders(Map<String, String> httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public static HttpHeaders from(List<String> lines) {
        Map<String, String> headers = lines.stream()
                .map(line -> line.split(KET_VALUE_DELIMITER))
                .collect(Collectors.toMap(line -> line[KEY_INDEX], line -> line[VALUE_INDEX]));

        return new HttpHeaders(headers);
    }

    public static HttpHeaders createDefaultHeaders(String requestNativePath, HttpBody httpBody) {
        Map<String, String> headers = new LinkedHashMap<>();

        headers.put(HeaderType.CONTENT_TYPE.getValue(), ContentType.extractValueFromPath(requestNativePath));
        headers.put(HeaderType.CONTENT_LENGTH.getValue(), String.valueOf(httpBody.getBytesLength()));

        return new HttpHeaders(headers);
    }

    public void setLocation(String value) {
        httpHeaders.put(HeaderType.LOCATION.getValue(), value);
    }

    public void addHeader(String key, String value) {
        httpHeaders.put(key, value);
    }

    public boolean containsKey(String key) {
        return httpHeaders.containsKey(key);
    }

    public String getHeaders() {
        return httpHeaders.keySet()
                .stream()
                .map(key -> String.format(HEADER_FORMAT, key, httpHeaders.get(key)))
                .collect(Collectors.joining("\r\n"));
    }

    public String get(String key) {
        if (containsKey(key)) {
            return httpHeaders.get(key);
        }

        return null;
    }

}
