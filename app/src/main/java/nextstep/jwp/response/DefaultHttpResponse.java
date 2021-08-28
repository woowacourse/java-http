package nextstep.jwp.response;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DefaultHttpResponse implements HttpResponse {

    private static final String RESPONSE_FORM = "%s\r\n\r\n%s\r\n\r\n%s";
    private static final String LINE_FORM = "%s %s %s";
    private static final String HEADER_FORM = "%s: %s";

    private StatusCode statusCode;
    private Map<String, String> headers;
    private StringBuilder content;

    public DefaultHttpResponse() {
        this.statusCode = StatusCode.OK;
        this.headers = new HashMap<>();
        this.content = new StringBuilder();
    }

    @Override
    public void addStatus(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public void addHeader(String key, String... values) {
        headers.put(key, String.join(", ", values));
    }

    @Override
    public void addBody(String content) {
        this.content.append(content);
    }

    @Override
    public String totalResponse() {
        return String.format(RESPONSE_FORM, statusLine(), headerLine(), content);
    }

    private String statusLine() {
        return String.format(LINE_FORM, "HTTP/1.1", statusCode.statusNumber(), statusCode.name());
    }

    private String headerLine() {
        return headers.entrySet().stream()
                .map(entry -> String.format(HEADER_FORM, entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("\r\n"));
    }
}
