package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestMessageParser {

    private final BufferedReader bufferedReader;

    public HttpRequestMessageParser(final BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    public HttpRequestMessage parseRequestMessage() throws IOException {
        String startLine = bufferedReader.readLine();
        if (startLine == null || startLine.isBlank()) {
            return new HttpRequestMessage(null, null, null);
        }

        List<String> headers = new ArrayList<>();
        for (String data = bufferedReader.readLine(); data != null; data = bufferedReader.readLine()) {
            if (data.isBlank()) {
                break;
            }
            headers.add(data);
        }

        Map<String, String> headerKeyValue = new HashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            final String[] keyValue = headers.get(i).split(": ");
            headerKeyValue.put(keyValue[0], keyValue[1]);
        }

        String body = null;
        if (startLine.startsWith("POST")) {
            int contentLength = Integer.parseInt(headerKeyValue.get("Content-Length"));
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            body = new String(buffer);
        }

        return new HttpRequestMessage(startLine, headerKeyValue, body);
    }
}
