package org.apache.http.response;

import org.apache.http.HttpVersion;

public class StartLine {

    private static final String START_LINE_PART_DELIMITER = " ";
    private static final String CRLF = "\r\n";
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
        return new StartLine(
                HttpVersion.valueOf(parts[VERSION_PART_ORDER]),
                HttpStatus.valueOf(parts[STATUS_PART_ORDER])
        );
    }

    @Override
    public String toString() {
        return version.getValue() + " " + httpStatus.toString() + CRLF;
    }
}
