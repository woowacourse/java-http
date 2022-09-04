package org.apache.coyote.http11.http11response;

import java.util.Map;

public class Http11ResponseHandler {

    private static final String LINE = "\r\n";

    public String makeResponse(Map<String, String> elements) {
        return String.join(LINE,
                "HTTP/1.1 200 OK ",
                "Content-Type: " + elements.get("Content-Type") + ";charset=utf-8 ",
                "Content-Length: " + elements.get("Content-Length") + " ",
                "",
                elements.get("body"));
    }
}
