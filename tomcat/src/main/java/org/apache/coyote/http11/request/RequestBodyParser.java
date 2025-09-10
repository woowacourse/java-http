package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class RequestBodyParser {

    private RequestBodyParser() {
    }
    
    public static String parse(BufferedReader reader, Map<String, String> headers) throws IOException {
        int contentLength = parseContentLength(headers.get("content-length"));
        
        StringBuilder body = new StringBuilder();
        for (int i = 0; i < contentLength; i++) {
            body.append((char) reader.read());
        }
        return body.toString();
    }
    
    private static int parseContentLength(String contentLengthStr) {
        if (contentLengthStr == null) {
            return 0;
        }
        
        try {
            return Math.max(Integer.parseInt(contentLengthStr), 0);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
