package nextstep.jwp.http.response;

public class HttpResponse {

    private static final String HTTP_REQUEST_LINE_FORMAT = "HTTP/1.1 %s %s ";
    private static final String HEADER_FORMAT = "%s: %s ";
    private static final String SEPARATE_LINE = "";

    private String statusLine = "";
    private ResponseHeaders responseHeaders = new ResponseHeaders();
    private String messageBody = "";

    public void addHeader(String key, String value) {
        responseHeaders.addAttribute(key, value);
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        statusLine = String.format(HTTP_REQUEST_LINE_FORMAT, httpStatus.code(), httpStatus.status());
    }

    public void setContent(String content, String contentType) {
        responseHeaders.addAttribute("Content-Type", contentType);
        responseHeaders.addAttribute("Content-Length", content.getBytes().length);

        messageBody = content;
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
