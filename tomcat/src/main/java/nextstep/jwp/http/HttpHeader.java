package nextstep.jwp.http;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.jwp.exception.HeaderFormatException;

public class HttpHeader {

    private static final String HEADER_SEPARATOR = ": ";
    private static final String LINE_SEPARATOR = "\r\n";
    private static final String BLANK = " ";
    private static final int SPLIT_SIZE = 2;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private Map<String, String> values;

    public HttpHeader() {
        this.values = new LinkedHashMap<>();
    }

    public HttpHeader(final List<String> headers) {
        this.values = new LinkedHashMap<>();

        for (String header : headers) {
            validateHeaderFormat(header);
            String[] value = header.split(HEADER_SEPARATOR, SPLIT_SIZE);
            addValue(value[KEY_INDEX], value[VALUE_INDEX]);
        }
    }

    private void validateHeaderFormat(final String header) {
        if (!header.contains(HEADER_SEPARATOR) || header.isBlank()) {
            throw new HeaderFormatException();
        }
        if (header.split(HEADER_SEPARATOR).length != 2) {
            throw new HeaderFormatException();
        }
    }

    public void addValue(final String key, final String value) {
        this.values.put(key, value);
    }

    public boolean hasContentLength() {
        String contentLength = values.get("Content-Length");
        return contentLength != null && !contentLength.equals("0");
    }

    public String getCookie() {
        return values.get("Cookie");
    }

    public String getContentLength() {
        return values.get("Content-Length");
    }

    public String createHeaderTemplate() {
        return values.entrySet().stream()
                .map(header -> header.getKey() + HEADER_SEPARATOR + header.getValue() + BLANK)
                .collect(Collectors.joining(LINE_SEPARATOR));
    }
}
