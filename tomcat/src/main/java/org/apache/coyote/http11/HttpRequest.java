package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private Map<String, String> requestInfo;

    public HttpRequest(BufferedReader bufferedReader) throws IOException {
        Map<String, String> tmp = new HashMap<>();

        String[] request = bufferedReader.readLine().split(" ");
        tmp.put("HTTP Method", request[0]);
        tmp.put("request URI", request[1]);
        tmp.put("HTTP Version", request[2]);

        String line = bufferedReader.readLine();
        while (!line.isEmpty()) {
            request = line.split(": ");
            tmp.put(request[0], request[1]);
            line = bufferedReader.readLine();
        }

        requestInfo = tmp;
    }

    public String get(String key) {
        return requestInfo.get(key);
    }
}
