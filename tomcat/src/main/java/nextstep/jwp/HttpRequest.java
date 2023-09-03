package nextstep.jwp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequest {

    private final Map<String, String> values = new HashMap<>();

    public HttpRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        boolean isStartLine = true;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            if (isStartLine) {
                String[] startLine = line.split(" ");
                values.put("method", startLine[0]);
                values.put("target", startLine[1]);
                values.put("version", startLine[2]);
                isStartLine = false;
            } else {
                String[] messages = line.split(":");
                values.put(messages[0], messages[1]);
            }
        }
    }

    public String getUri() {
        String target = values.get("target");
        int queryStringIdx = target.indexOf("?");
        if (queryStringIdx == -1) {
            return target;
        }

        return target.substring(0, queryStringIdx);
    }

    public Map<String, String> getQueryString() {
        String target = values.get("target");
        int queryStringIdx = target.indexOf("?");
        if (queryStringIdx == -1) {
            throw new IllegalStateException();
        }

        String queryStrings = target.substring(queryStringIdx + 1);
        return Arrays.stream(queryStrings.split("&"))
           .map(keyAndValue -> keyAndValue.split("="))
           .collect(Collectors.toMap(
               keyAndValue -> keyAndValue[0],
               keyAndValue -> keyAndValue[1]));
    }
}
