package nextstep.jwp.http.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ResponseHeader {

    private HashMap<String, ArrayList<String>> headers = new LinkedHashMap<>();

    public void addHeader(String header, String content) {
        ArrayList<String> contents = headers.getOrDefault(header, new ArrayList<>());
        contents.add(content);

        headers.put(header, contents);
    }

    public Map<String, ArrayList<String>> getHeaders() {
        return headers;
    }

    public String asString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (Entry<String, ArrayList<String>> header : headers.entrySet()) {
            stringBuilder
                    .append(String.format("%s: ", header.getKey()))
                    .append(String.join(",", header.getValue()))
                    .append(" \r\n");
        }

        return stringBuilder.toString();
    }
}
