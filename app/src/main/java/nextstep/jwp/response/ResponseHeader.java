package nextstep.jwp.response;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponseHeader {

    private static final String HEADER_FORMAT = "%s: %s ";
    private static final String LINE_SEPARATOR = "\r\n";

    private Map<String, String> headers = new HashMap<>();

    public void addHeader(String name, String value) {
        this.headers.put(name, value);
    }

    public String toResponseHeaders() {
        return headers.entrySet().stream()
                .map(entrySet -> String.format(HEADER_FORMAT, entrySet.getKey(), entrySet.getValue()))
                .collect(Collectors.joining(LINE_SEPARATOR));
    }
}
