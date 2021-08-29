package nextstep.jwp;

import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {
    private final Map<String, String> headers = new HashMap<>();

    public HttpHeaders(String httpHeaders) {
        String[] splitHeaders = httpHeaders.split("\r\n");

        for (String header : splitHeaders) {
            String[] splitKeyValue = header.split(": ", 2);
            headers.put(splitKeyValue[0], splitKeyValue[1]);
        }
    }

    public String get(String key) {
        return headers.get(key);
    }
}
