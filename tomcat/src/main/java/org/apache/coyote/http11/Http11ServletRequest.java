package org.apache.coyote.http11;

import java.util.List;

public record Http11ServletRequest(String method, String path, String protocolVersion) {

    public static Http11ServletRequest parse(List<String> lines) {
        String[] startLineParts = lines.getFirst().split(" ");
        return new Http11ServletRequest(startLineParts[0], startLineParts[1], startLineParts[2]);
    }
}
