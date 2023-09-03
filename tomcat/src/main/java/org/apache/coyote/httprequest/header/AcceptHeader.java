package org.apache.coyote.httprequest.header;

import java.util.List;

public class AcceptHeader implements RequestHeader {

    private static final String DELIMITER = ",";

    private final List<String> mediaTypes;

    private AcceptHeader(final List<String> mediaTypes) {
        this.mediaTypes = mediaTypes;
    }

    public static AcceptHeader from(final String printedMediaTypes) {
        return new AcceptHeader(parseByDelimiter(printedMediaTypes));
    }

    private static List<String> parseByDelimiter(final String mediaTypes) {
        return List.of(mediaTypes.split(DELIMITER));
    }

    @Override
    public String getValue() {
        return String.join(DELIMITER, mediaTypes);
    }
}
