package org.apache.coyote.request.requestLine.protocolVersion;

import java.util.regex.Pattern;

public class Version {

    private static final Pattern VERSION_PATTERN = Pattern.compile("^[0-9]+(\\.[0-9]+)*$");
    private static final String HTTP_1_VERSION = "1.1";

    private String version;

    public static Version from(final String version) {
        validateVersion(version);
        return new Version(version);
    }

    public static Version fromHTTP1() {
        return new Version(HTTP_1_VERSION);
    }

    private Version(final String version) {
        this.version = version;
    }

    private static void validateVersion(final String version) {
        if (version == null || !VERSION_PATTERN.matcher(version).matches()) {
            throw new IllegalArgumentException("[ERROR] 프로토콜의 버전이 잘못되었습니다.");
        }
    }

    public String getVersion() {
        return version;
    }
}
