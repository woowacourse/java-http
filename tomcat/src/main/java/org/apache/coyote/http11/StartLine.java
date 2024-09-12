package org.apache.coyote.http11;

public class StartLine {

    private final String method;
    private final String path;
    private final String protocolVersion;

    public StartLine(final String method, final String path, final String protocolVersion) {
        this.method = method;
        this.path = path;
        this.protocolVersion = protocolVersion;
    }

    public static StartLine from(final String startLine) {
        if (startLine == null || startLine.isBlank()) {
            return new StartLine(null, null, null);
        }
        String[] elements = startLine.split(" ");
        return new StartLine(elements[0], elements[1], elements[2]);
    }

    public boolean isNull() {
        return isNull(method) || isNull(path) || isNull(protocolVersion);
    }

    private boolean isNull(final String value) {
        return value == null || value.isBlank();
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }
}
