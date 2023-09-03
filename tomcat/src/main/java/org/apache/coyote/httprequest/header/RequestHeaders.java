package org.apache.coyote.httprequest.header;
import org.apache.coyote.httprequest.exception.InvalidHeaderException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHeaders {

    private static final String DELIMITER = ": ";
    private static final int PARSED_HEADER_SIZE = 2;
    private static final int HEADER_KEY_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;

    private final Map<RequestHeaderType, RequestHeader> headers;

    private RequestHeaders(final Map<RequestHeaderType, RequestHeader> headers) {
        this.headers = headers;
    }

    public static RequestHeaders from(final BufferedReader bufferedReader) throws IOException {
        final Map<RequestHeaderType, RequestHeader> headers = new HashMap<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            final List<String> parsedHeader = parseByDelimiter(line);
            final RequestHeaderType headerType = RequestHeaderType.from(parsedHeader.get(HEADER_KEY_INDEX));
            headers.put(headerType, headerType.saveRequestHeader(parsedHeader.get(HEADER_VALUE_INDEX)));
        }
        return new RequestHeaders(headers);
    }

    private static List<String> parseByDelimiter(final String line) {
        final List<String> parsedRequestHeader = List.of(line.split(DELIMITER));
        if (parsedRequestHeader.size() != PARSED_HEADER_SIZE) {
            throw new InvalidHeaderException();
        }
        return parsedRequestHeader;
    }
}
