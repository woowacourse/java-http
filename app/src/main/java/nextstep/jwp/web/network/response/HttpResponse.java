package nextstep.jwp.web.network.response;


import nextstep.jwp.web.controller.View;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {

    private static final String RESPONSE_FORMAT = "%s\r\n%s\r\n\r\n%s";
    private static final String HEADER_FORMAT = "%s: %s ";

    private StatusLine statusLine;
    private final Map<String, String> headers;
    private String body;

    public HttpResponse() {
        this.statusLine = new StatusLine(HttpStatus.OK);
        this.headers = new LinkedHashMap<>();
        this.body = "";
    }

    public String print() {
        return String.format(RESPONSE_FORMAT, statusLine.print(), headersFields(), body);
    }

    private String headersFields() {
        return headers.entrySet().stream()
                .map(entry -> String.format(HEADER_FORMAT, entry.getKey(), entry.getValue()))
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
