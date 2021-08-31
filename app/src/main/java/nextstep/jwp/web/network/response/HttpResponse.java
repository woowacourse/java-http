package nextstep.jwp.web.network.response;


import nextstep.jwp.web.controller.View;

public class HttpResponse {

    private StatusLine statusLine;
    private ContentType contentType;
    private int contentLength;
    private String body;

    public HttpResponse() {
        this.statusLine = new StatusLine(HttpStatus.OK);
        this.contentType = ContentType.HTML;
        this.contentLength = 0;
        this.body = "";
    }

    public String asString() {
        return String.join("\r\n",
                statusLine.asString(),
                "Content-Type: " + contentType.getType() + " ",
                "Content-Length: " + contentLength + " ",
                "",
                body);
    }

    public void setStatus(HttpStatus status) {
        this.statusLine.setStatus(status);
    }

    public void setBody(View view) {
        this.body = view.render();
        this.contentType = view.getContentType();
        this.contentLength = this.body.getBytes().length;
    }
}

