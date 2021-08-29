package nextstep.jwp.webserver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHeaders {

    private final Map<String, List<String>> headers;

    public HttpHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public HttpHeaders(List<String> headerList) {
        this(parseHeaders(headerList));
    }

    private static Map<String, List<String>> parseHeaders(List<String> headerList) {
        Map<String, List<String>> headers = new HashMap<>();
        for (String header : headerList) {
            String[] keyValue = header.split(":");
            headers.put(keyValue[0], Arrays.asList(keyValue[1].split(",")));
        }
        return headers;
    }

    public String get(String header) {
        return getHeaders(header).get(0);
    }

    public List<String> getHeaders(String header) {
        return headers.get(header);
    }
}
