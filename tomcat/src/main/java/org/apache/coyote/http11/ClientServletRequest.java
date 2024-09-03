package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ClientServletRequest {

    private final String method;
    private final String path;
    private final String protocolVersion;

    public ClientServletRequest(InputStream inputStream) throws IOException {
        List<String> lines = InputStreamConvertor.convertToLines(inputStream, StandardCharsets.UTF_8);

        String[] startLineParts = lines.getFirst().split(" ");
        method = startLineParts[0];
        path = startLineParts[1];
        protocolVersion = startLineParts[2];
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

    @Override
    public String toString() {
        return "ClientServletRequest{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", protocolVersion='" + protocolVersion + '\'' +
                '}';
    }
}
