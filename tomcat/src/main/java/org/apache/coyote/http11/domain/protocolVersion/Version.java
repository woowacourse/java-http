package org.apache.coyote.http11.domain.protocolVersion;

import java.util.regex.Pattern;

public class Version {

    private static final Pattern VERSION_PATTERN = Pattern.compile("^[0-9]+(\\.[0-9]+)*$");

    private String version;

    public Version(String version) {
        validateVersion(version);
        this.version = version;
    }

    private void validateVersion(String version) {
        if (version == null) {
            throw new NullPointerException("프로토콜의 버전이 Null 입니다.");
        }
        validateMadeOfNumber(version);
    }

    private void validateMadeOfNumber(String version) {
        if (!VERSION_PATTERN.matcher(version).matches()) {
            throw new IllegalArgumentException("버전은 숫자여야 합니다.");
        }
    }

    public String getVersion() {
        return version;
    }
}
