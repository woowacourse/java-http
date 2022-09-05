package nextstep.jwp.http;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpHeader {

    private static final String HEADER_SEPARATOR = ": ";
    private static final String LINE_SEPARATOR = "\r\n";
    private static final String BLANK = " ";
    private static final int SPLIT_SIZE = 2;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private Map<String, String> headers;

    public HttpHeader() {
        this.headers = new LinkedHashMap<>();
    }

    public HttpHeader(final List<String> headers) {
        this.headers = new LinkedHashMap();

        for (String header : headers) {
            String[] headerLine = header.split(HEADER_SEPARATOR, SPLIT_SIZE);
            addHeader(headerLine[KEY_INDEX], headerLine[VALUE_INDEX]);
        }
    }

    public void addHeader(final String key, final String value) {
        this.headers.put(key, value);
    }

    public String createHeaderTemplate() {
        return headers.entrySet().stream()
                .map(header -> header.getKey() + HEADER_SEPARATOR + header.getValue() + BLANK)
                .collect(Collectors.joining(LINE_SEPARATOR));
    }

    public String get(final String key) {
        return headers.get(key);
    }

    public String getCookie() {
        return headers.get("Cookie");
    }
}
