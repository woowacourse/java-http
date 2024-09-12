package org.apache.coyote.protocolVersion;

import java.util.Objects;
import java.util.regex.Pattern;

public class Version {

    private static final Pattern VERSION_PATTERN = Pattern.compile("^[0-9]+(\\.[0-9]+)*$");

    private final String version;

    public Version(String version) {
        validateVersion(version);
        this.version = version;
    }

    private void validateVersion(String version) {
        if (version == null) {
            throw new NullPointerException("프로토콜의 버전이 존재하지 않습니다.");
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Version version1 = (Version) o;
        return Objects.equals(version, version1.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version);
    }
}
