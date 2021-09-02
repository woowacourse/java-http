package nextstep.jwp.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RequestHeader {
    private static final String HEADER_DELIMITER = ":";
    private static final String LAST_OF_HEADER_LINE = "";

    private final Map<String, String> requestHeaders;

    public RequestHeader(Map<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public static RequestHeader of(BufferedReader bufferedReader) throws IOException {
        final Map<String, String> requestHeaders = new HashMap<>();
        String line;
        while (!LAST_OF_HEADER_LINE.equals(line = bufferedReader.readLine()) && Objects.nonNull(line)) {
            final int indexOfHeaderDelimiter = line.indexOf(HEADER_DELIMITER);
            final String headerName = line.substring(0, indexOfHeaderDelimiter).trim();
            final String headerValue = line.substring(indexOfHeaderDelimiter + 1).trim();
            requestHeaders.put(headerName, headerValue);
        }
        return new RequestHeader(requestHeaders);
    }

    public String getHeader(String name) {
        return requestHeaders.get(name);
    }

    public boolean contains(String name) {
        return requestHeaders.containsKey(name);
    }
}
