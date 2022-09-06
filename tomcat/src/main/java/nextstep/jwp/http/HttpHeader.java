package nextstep.jwp.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

    public HttpHeader(final BufferedReader bufferReader) throws IOException {
        this.values = new LinkedHashMap<>();

        String headerLine = bufferReader.readLine();
        while (isValidHeaderForm(headerLine)) {
            String[] value = headerLine.split(HEADER_SEPARATOR, SPLIT_SIZE);
            addValue(value[KEY_INDEX], value[VALUE_INDEX]);
            headerLine = bufferReader.readLine();
        }
    }

    private boolean isValidHeaderForm(final String headerLine) {
        return headerLine != null && !headerLine.isBlank();
    }

    public void addValue(final String key, final String value) {
        this.values.put(key, value);
    }

    public String createHeaderTemplate() {
        return values.entrySet().stream()
                .map(header -> header.getKey() + HEADER_SEPARATOR + header.getValue() + BLANK)
                .collect(Collectors.joining(LINE_SEPARATOR));
    }

    public String getValues(final String key) {
        return values.get(key);
    }

    public String getCookie() {
        return values.get("Cookie");
    }
}
