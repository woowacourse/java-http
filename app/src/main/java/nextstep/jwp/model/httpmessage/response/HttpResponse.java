package nextstep.jwp.model.httpmessage.response;

import java.io.OutputStream;
import java.util.Map;

public class HttpResponse {

    public static final String EMPTY_LINE = "";

    private final OutputStream outputStream;
    private final ResponseHeader headers = new ResponseHeader();
    private ResponseLine responseLine;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public Map<Object, String> getHeaders() {
        return headers.getAllHeaders();
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public ResponseLine getResponseLine() {
        return responseLine;
    }

    public String getHeader(String value) {
        return headers.getHeader(value);
    }

    public String getContentType() {
        return headers.getContentType();
    }

    public void setContentType(String value) {
        headers.setContentType(value);
    }

    public int getContentLength() {
        return headers.getContentLength();
    }

    public void setContentLength(int length) {
        headers.setContentLength(length);
    }

    public void addHeader(ResponseHeaderType type, String value) {
        headers.add(type, value);
    }

    public HttpStatus getStatus() {
        return responseLine.getStatus();
    }

    public void setStatus(HttpStatus status) {
        responseLine = new ResponseLine(status);
    }
}
