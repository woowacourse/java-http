package nextstep.jwp.web.network.response;


import nextstep.jwp.web.controller.View;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {

    private static final String DEFAULT_BODY = "";

    private final StatusLine statusLine;
    private final Map<String, String> headers;
    private String body;

    public HttpResponse() {
        this.statusLine = new StatusLine(HttpStatus.OK);
        this.headers = new LinkedHashMap<>();
        this.body = DEFAULT_BODY;
    }

    public String print() {
        return String.format("%s\r%n%s\r%n\r%n%s", statusLine.print(), headersFields(), body);
    }

    private String headersFields() {
        return headers.entrySet().stream()
                .map(entry -> String.format("%s: %s ", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("\r\n"));

    }

    public void setStatus(HttpStatus status) {
        this.statusLine.setStatus(status);
    }

    public void setBody(View view) {
        this.body = view.render();
        headers.put("Content-Type", view.getContentType().getType());
        headers.put("Content-Length", String.valueOf(this.body.getBytes().length));
    }

    public void setHeader(String key, String value) {
        this.headers.put(key, value);
    }
}
