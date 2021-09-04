package nextstep.jwp.http.response;

import nextstep.jwp.http.session.HttpSession;
import nextstep.jwp.http.session.HttpSessions;

public class HttpResponse {

    private static final String SESSION_ITEM_FORMAT = "%s=%s";
    private static final String HTTP_STATUS_LINE_FORMAT = "HTTP/1.1 %s %s ";
    private static final String HEADER_FORMAT = "%s: %s ";
    private static final String SEPARATE_LINE = "";

    private final ResponseHeaders responseHeaders = new ResponseHeaders();
    private HttpStatus httpStatus;
    private String messageBody = "";

    public void addHeader(String key, String value) {
        responseHeaders.addAttribute(key, value);
    }

    public String getHeader(String name) {
        return responseHeaders.getAttribute(name);
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
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
        String statusLine = String.format(HTTP_STATUS_LINE_FORMAT, httpStatus.code(), httpStatus.status());
        String headerLine = String.join("\r\n", responseHeaders.asLines(HEADER_FORMAT));
        return String.join("\r\n",
                statusLine,
                headerLine,
                SEPARATE_LINE,
                messageBody);
    }

    public HttpStatus status() {
        return httpStatus;
    }

    public void addSession(HttpSession session) {
        if(!HttpSessions.contains(session.getId())){
            HttpSessions.addSession(session);
            String sessionItem = String.format(SESSION_ITEM_FORMAT, session.getType(), session.getId());
            responseHeaders.addAttribute("Set-Cookie", sessionItem);
        }
    }
}
