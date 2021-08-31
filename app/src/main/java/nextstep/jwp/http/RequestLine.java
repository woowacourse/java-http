package nextstep.jwp.http;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RequestLine {
    private final HttpMethod method;
    private String uri;
    private Map<String, String> params = new HashMap<>();

    public RequestLine(String requestLine) {
        final String[] tokens = requestLine.split(" ");
        method = HttpMethod.valueOf(tokens[0]);

        if (method.isPost()) {
            uri = tokens[1];
            return;
        }

        int index = tokens[1].indexOf("?");
        if (index == -1) {
            uri = tokens[1];
        } else {
            uri = tokens[1].substring(0, index);
            parseValues(tokens[1].substring(index + 1));
        }
    }

    private void parseValues(String values) {
        String[] tokens = values.split("&");
        Arrays.stream(tokens).forEach(this::putDataFromQueryString);
    }

    private void putDataFromQueryString(String data) {
        params.put(data.substring(0, data.indexOf("=")), data.substring(data.indexOf("=") + 1));
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
