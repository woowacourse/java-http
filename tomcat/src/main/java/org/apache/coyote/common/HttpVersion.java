package org.apache.coyote.common;

import org.apache.coyote.httprequest.exception.InvalidHttpVersionException;

import java.util.List;

public class HttpVersion {

    private static final String PROTOCOL_PREFIX = "HTTP/";
    private static final char VERSION_DELIMITER = '.';
    private static final int MAIN_VERSION_INDEX = 0;
    private static final int SUB_VERSION_INDEX = 1;
    private static final int START_INDEX = 0;
    private static final int NEXT_INDEX = 1;

    private final int mainVersion;
    private final int subVersion;

    private HttpVersion(final int mainVersion, final int subVersion) {
        this.mainVersion = mainVersion;
        this.subVersion = subVersion;
    }

    public static HttpVersion from(final String input) {
        final String version = extractProtocolVersion(input);
        try {
            final List<Integer> splitVersion = splitMainAndSubVersion(version);
            return new HttpVersion(splitVersion.get(MAIN_VERSION_INDEX), splitVersion.get(SUB_VERSION_INDEX));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            throw new InvalidHttpVersionException();
        }
    }

    private static String extractProtocolVersion(final String input) {
        if (!input.contains(PROTOCOL_PREFIX)) {
            throw new InvalidHttpVersionException();
        }
        return input.substring(PROTOCOL_PREFIX.length());
    }

    private static List<Integer> splitMainAndSubVersion(final String version) {
        final int indexOfDelimiter = version.indexOf(VERSION_DELIMITER);
        final int mainVersion = Integer.parseInt(version.substring(START_INDEX, indexOfDelimiter));
        final int subVersion = Integer.parseInt(version.substring(indexOfDelimiter + NEXT_INDEX));
        return List.of(mainVersion, subVersion);
    }

    public String getValue() {
        return PROTOCOL_PREFIX + mainVersion + VERSION_DELIMITER + subVersion;
    }
}
