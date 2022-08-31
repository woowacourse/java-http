package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequest {
    private final Map<String, String> requestInfo;

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

    public String getPath() {
        String uri = requestInfo.get("request URI");

        int index = uri.indexOf("?");

        if (index != -1) {
            return uri.substring(0, index);
        }
        return uri;
    }

    public Map<String, String> getQueryParams() {
        String uri = requestInfo.get("request URI");

        int index = uri.indexOf("?");
        if (index != -1) {
            String queryString = uri.substring(index + 1);

            return Arrays.stream(queryString.split("&"))
                .map(query -> query.split("="))
                .collect(Collectors.toMap(query -> query[0], query -> query[1]));
        }

        return Map.of();
    }

    public boolean hasParam() {
        return !getQueryParams().isEmpty();
    }
}
