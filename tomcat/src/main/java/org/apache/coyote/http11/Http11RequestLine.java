package org.apache.coyote.http11;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class Http11RequestLine {

    private static final List<String> METHODS =
            List.of("POST", "GET", "HEAD", "PUT", "PATCH", "DELETE", "CONNECT", "TRACE", "OPTIONS");
    private static final String PROTOCOL = "HTTP";
    private static final String PROTOCOL_VERSION = "1.1";
    private static final int REQUEST_LINE_LENGTH = 3;
    private static final int VERSION_OF_PROTOCOL_LENGTH = 2;
    private static final String VERSION_OF_PROTOCOL_DELIMITER = "/";

    private final Map<String, String> line;


    public Http11RequestLine(String line) {
        validate(line);
        this.line = new HashMap<>();
        this.line.put("Method", line.split(" ")[0]);
        this.line.put("Path", line.split(" ")[1]);
        this.line.put("Protocol", line.split(" ")[2]);
    }

    private void validate(String line) {
        if (StringUtils.isBlank(line) || line.split(" ").length != REQUEST_LINE_LENGTH) {
            throw new IllegalArgumentException();
        }
        validateMethod(line);
        validateProtocol(line);
    }

    private void validateMethod(String startLine) {
        String method = startLine.split(" ")[0];
        if (!METHODS.contains(method)) {
            throw new IllegalArgumentException();
        }
    }

    private void validateProtocol(String startLine) {
        String protocol = startLine.split(" ")[2];
        if (protocol.split(VERSION_OF_PROTOCOL_DELIMITER).length != VERSION_OF_PROTOCOL_LENGTH) {
            throw new IllegalArgumentException();
        }
        if (!PROTOCOL.equals(protocol.split(VERSION_OF_PROTOCOL_DELIMITER)[0])) {
            throw new IllegalArgumentException();
        }
        if (!PROTOCOL_VERSION.equals(protocol.split(VERSION_OF_PROTOCOL_DELIMITER)[1])) {
            throw new IllegalArgumentException();
        }
    }

    public String getMethod() {
        return line.get("Method");
    }

    public String getPath() {
        return line.get("Path");
    }
}
