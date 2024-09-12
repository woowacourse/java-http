package org.apache.http.response;

import java.util.Objects;

import org.apache.http.HttpVersion;

public class StartLine {

    private static final String START_LINE_PART_DELIMITER = " ";
    private static final String CRLF = "\r\n";
    private static final int START_LINE_PART_SIZE = 3;
    private static final int VERSION_PART_ORDER = 0;
    private static final int STATUS_PART_ORDER = 1;

    private final HttpVersion version;
    private final HttpStatus httpStatus;

    public StartLine(HttpVersion version, HttpStatus httpStatus) {
        this.version = version;
        this.httpStatus = httpStatus;
    }

    public static StartLine from(String startLine) {
        String[] parts = startLine.split(START_LINE_PART_DELIMITER);
        checkStartLinePartSize(parts.length);

        return new StartLine(
                HttpVersion.getHttpVersion(parts[VERSION_PART_ORDER]),
                HttpStatus.getHttpStatus(Integer.parseInt(parts[STATUS_PART_ORDER]))
        );
    }

    private static void checkStartLinePartSize(int startLinePartsSize) {
        if (startLinePartsSize != START_LINE_PART_SIZE) {
            throw new IllegalArgumentException("StartLine 형식이 맞지 않습니다. version, status로 구성해주세요.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StartLine startLine = (StartLine) o;
        return version == startLine.version && httpStatus == startLine.httpStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, httpStatus);
    }

    @Override
    public String toString() {
        return version.getValue() + " " + httpStatus.toString() + CRLF;
    }
}
