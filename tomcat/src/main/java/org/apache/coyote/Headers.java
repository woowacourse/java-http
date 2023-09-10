package org.apache.coyote;

import static org.apache.coyote.utils.Constant.EMPTY;
import static org.apache.coyote.utils.Constant.HEADER_DELIMITER;
import static org.apache.coyote.utils.Constant.KEY_INDEX;
import static org.apache.coyote.utils.Constant.LINE_SEPARATOR;
import static org.apache.coyote.utils.Constant.SPLIT_LIMIT_SIZE;
import static org.apache.coyote.utils.Constant.VALUE_INDEX;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Headers {

    private final Map<String, String> headers;

    private Headers(final Map<String, String> headers) {
        this.headers = headers;
    }

    public Headers() {
        this.headers = new LinkedHashMap<>();
    }

    public static Headers from(final List<String> lines) {
        final Map<String, String> headers = new HashMap<>();
        for (final String line : lines) {
            if (EMPTY.equals(line)) {
                break;
            }
            final String[] header = line.split(HEADER_DELIMITER, SPLIT_LIMIT_SIZE);
            headers.put(header[KEY_INDEX], header[VALUE_INDEX]);
        }

        return new Headers(headers);
    }

    public void addHeader(final Header header, final String value) {
        headers.put(header.getName(), value);
    }


    public String getValueOf(final Header header) {
        return headers.get(header.getName());
    }

    public String stringify() {
        return headers.entrySet().stream()
                .map(entry -> entry.getKey() + HEADER_DELIMITER + entry.getValue())
                .collect(Collectors.joining(LINE_SEPARATOR)) + " ";
    }
}
