package org.apache.coyote.common;

import org.apache.coyote.httprequest.exception.InvalidHttpVersionException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HttpVersion {

    private static final String PROTOCOL_PREFIX = "HTTP/";
    private static final String VERSION_DELIMITER = "\\.";
    private static final int NEXT_INDEX = 1;
    private static final int MAIN_VERSION_INDEX = 0;
    private static final int SUB_VERSION_INDEX = 1;

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
        return input.substring(PROTOCOL_PREFIX.length() + NEXT_INDEX);
    }

    private static List<Integer> splitMainAndSubVersion(final String version) {
        return Arrays.stream(version.split(VERSION_DELIMITER))
                .map(Integer::parseInt)
                .collect(Collectors.toUnmodifiableList());
    }

    public String getValue() {
        return PROTOCOL_PREFIX + mainVersion + VERSION_DELIMITER + subVersion;
    }
}
