package org.apache.tomcat.http.common;

import java.util.List;
import java.util.Objects;

public class Version {

    private static final String PROTOCOL_NAME = "HTTP";
    private static final String DELIMITER = "[/.]";

    private final int major;
    private final int minor;

    public Version(final String plaintext) {
        final var elements = List.of(plaintext.split(DELIMITER));
        validateProtocol(elements);
        validateVersion(Integer.parseInt(elements.get(1)), Integer.parseInt(elements.get(2)));
        this.major = Integer.parseInt(elements.get(1));
        this.minor = Integer.parseInt(elements.get(2));
    }

    public Version(final int major, final int minor) {
        this.major = major;
        this.minor = minor;
    }

    private void validateProtocol(final List<String> elements) {
        if (elements.getFirst().equals(PROTOCOL_NAME) && elements.size() == 3) {
            return;
        }
        throw new IllegalArgumentException("올바르지 않은 버전 정보");

    }

    private void validateVersion(final int major, final int minor) {
        if (major == 1 && minor == 1) {
            return;
        }
        throw new IllegalArgumentException("올바르지 않은 Http version");
    }

    public String getResponseText() {
        return PROTOCOL_NAME + "/" + major + "." + minor;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Version version = (Version) o;
        return major == version.major && minor == version.minor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor);
    }
}
