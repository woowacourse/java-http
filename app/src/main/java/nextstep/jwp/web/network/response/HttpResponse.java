package nextstep.jwp.web.network.response;


import nextstep.jwp.web.controller.View;

public class HttpResponse {

    private static final String DEFAULT_BODY = "";

    private final StatusLine statusLine;
    private final HttpHeaders headers;
    private String body;

    public HttpResponse() {
        this.statusLine = new StatusLine(HttpStatus.OK);
        this.headers = new HttpHeaders();
        this.body = DEFAULT_BODY;
    }

    public String print() {
        return String.format("%s\r%n%s\r%n\r%n%s", statusLine.print(), headersFields(), body);
    }

    private String headersFields() {
        return headers.getAll();
    }

    public void setStatus(HttpStatus status) {
        this.statusLine.setStatus(status);
    }

    public void setBody(View view) {
        this.body = view.render();
        headers.put("Content-Type", view.getContentType());
        headers.put("Content-Length", String.valueOf(this.body.getBytes().length));
    }

    public void setHeader(String key, String value) {
        this.headers.put(key, value);
    }
}
