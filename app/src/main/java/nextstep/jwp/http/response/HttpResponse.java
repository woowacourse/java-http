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

    public HttpResponse(String statusLine, ResponseHeaders responseHeaders, String messageBody) {
        this.statusLine = statusLine;
        this.responseHeaders = responseHeaders;
        this.messageBody = messageBody;
    }

    public HttpResponse(String statusLine, ResponseHeaders responseHeaders) {
        this(statusLine, responseHeaders, "");
    }

    public static HttpResponse of(Model model, View view) {
        HttpStatus httpStatus = model.httpStatus();
        if (httpStatus.isFound()) {
            return redirect(model);
        }

        String statusLine = String.format(HTTP_REQUEST_LINE_FORMAT, httpStatus.code(), httpStatus.name());

        ResponseHeaders headers = new ResponseHeaders();
        headers.addAttribute("Content-Type", view.contentType());
        headers.addAttribute("Content-Length", view.contentLength());

        return new HttpResponse(statusLine, headers, view.content());
    }

    public static HttpResponse redirect(Model model) {
        HttpStatus httpStatus = HttpStatus.FOUND;
        String statusLine = String.format(HTTP_REQUEST_LINE_FORMAT, httpStatus.code(), httpStatus.name());

        ResponseHeaders headers = new ResponseHeaders();
        headers.addAttribute("Location", model.location());

        return new HttpResponse(statusLine, headers);
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
