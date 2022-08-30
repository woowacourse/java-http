package org.apache.coyote;

import java.util.Optional;

public class Request {

    private static final String START_LINE_DELIMITER = " ";
    private static final String EXTENSION_DELIMITER = "\\.";
    private static final int FIRST_LINE_LENGTH = 3;
    private static final int NO_EXTENSION_STANDARD = 1;

    private String method;
    private String requestUrl;
    private String version;

    public Request(final String method, final String requestUrl, final String version) {
        this.method = method;
        this.requestUrl = requestUrl;
        this.version = version;
    }

    public static Request from(final String startLine) {
        String[] line = startLine.split(START_LINE_DELIMITER);
        if (line.length != FIRST_LINE_LENGTH) {
            throw new HttpRequestStartLineNotValidException();
        }
        return new Request(line[0], line[1], line[2]);
    }

    public Optional<String> getRequestExtension() {
        String[] nameExtension = requestUrl.split(EXTENSION_DELIMITER);
        if (nameExtension.length == NO_EXTENSION_STANDARD) {
            return Optional.empty();
        }
        return Optional.of(nameExtension[1]);
    }

    public String getMethod() {
        return method;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public String getVersion() {
        return version;
    }
}
