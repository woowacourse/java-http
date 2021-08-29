package nextstep.jwp.http.response;

import nextstep.jwp.handler.modelandview.Model;
import nextstep.jwp.view.View;

public class HttpResponse {

    private static final String HTTP_REQUEST_LINE_FORMAT = "HTTP/1.1 %s %s ";
    private static final String HEADER_FORMAT = "%s: %s ";
    private static final String SEPARATE_LINE = "";

    private String statusLine;
    private ResponseHeaders responseHeaders;
    private String messageBody;

    public HttpResponse() {
    }

    public void setResult(Model model, View view) {
        HttpStatus httpStatus = model.httpStatus();
        String statusLine = String.format(HTTP_REQUEST_LINE_FORMAT, httpStatus.code(), httpStatus.name());

        ResponseHeaders headers = new ResponseHeaders();

        if (!view.isEmpty()) {
            headers.addAttribute("Content-Type", view.contentType());
            headers.addAttribute("Content-Length", view.contentLength());
        }

        if (model.contains("Location")) {
            headers.addAttribute("Location", (String) model.getAttribute("Location"));
        }

        this.statusLine = statusLine;
        this.responseHeaders = headers;
        this.messageBody = view.content();
    }

    public byte[] responseAsBytes() {
        return responseAsString().getBytes();
    }

    public String responseAsString() {
        return String.join("\r\n",
                statusLine,
                String.join("\r\n", responseHeaders.asLines(HEADER_FORMAT)),
                SEPARATE_LINE,
                messageBody);
    }
}
