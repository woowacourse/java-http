package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RequestLine {
    private final HttpMethod method;
    private final String uri;
    private Map<String, String> params = new HashMap<>();

    public RequestLine(String requestLine) {
        final String[] tokens = requestLine.split(" ");
        method = HttpMethod.valueOf(tokens[0]);

        if (method.isPost()) {
            uri = tokens[1];
            return;
        }

        int index = tokens[1].indexOf("?");
        uri = getUriAndParamsFromRequestLine(tokens, index);
    }

    private String getUriAndParamsFromRequestLine(String[] tokens, int index) {
        if (index == -1) {
            return tokens[1];
        }

        String queryString = tokens[1].substring(index + 1);
        params = Stream.of(queryString.split("&"))
                .map(x -> x.split("="))
                .collect(Collectors.toMap(x -> x[0], x -> x[1]));
        return tokens[1].substring(0, index);
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

    public String getParameter(String name) {
        return params.get(name);
    }
}
