package org.apache.coyote.http11.response;

import java.util.Map;
import java.util.Map.Entry;

public class Http11Response {

    private final String protocolVersion;
    private final int statusCode;
    private final String statusMessage;
    private final Map<String, String> responseHeader;
    private final String body;

    public Http11Response(String protocolVersion, int statusCode, String statusMessage,
                          Map<String, String> responseHeader,
                          String body) {
        this.protocolVersion = protocolVersion;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.responseHeader = responseHeader;
        this.body = body;
    }

    public String toMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(protocolVersion + " " + statusCode + " " + statusMessage + " \r\n");
        for (Entry<String, String> entry : responseHeader.entrySet()) {
            stringBuilder.append(entry.getKey() + ": " + entry.getValue() + " \r\n");
        }
        stringBuilder.append("\r\n");
        stringBuilder.append(body);
        stringBuilder.append("\r\n");

        return stringBuilder.toString();
    }
}
