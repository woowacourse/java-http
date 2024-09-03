package org.apache.coyote.http11;

import java.util.List;

public record ClientServletRequest(String method, String path, String protocolVersion) {

    public static ClientServletRequest parse(List<String> lines) {
        String[] startLineParts = lines.getFirst().split(" ");
        return new ClientServletRequest(startLineParts[0], startLineParts[1], startLineParts[2]);
    }
}
