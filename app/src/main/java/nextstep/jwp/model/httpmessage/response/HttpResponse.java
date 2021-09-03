package nextstep.jwp.model.httpmessage.response;

import nextstep.jwp.model.httpmessage.session.HttpCookie;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static nextstep.jwp.model.httpmessage.response.ResponseHeaderType.COOKIE;

public class HttpResponse {

    private final OutputStream outputStream;
    private final ResponseHeader headers = new ResponseHeader();
    private final List<HttpCookie> cookies = new ArrayList<>();
    private ResponseLine responseLine;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public Map<String, String> getHeaders() {
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
        headers.add(type.value(), value);
    }

    public HttpStatus getStatus() {
        return responseLine.getStatus();
    }

    public void setResponseLine(HttpStatus status, String protocol) {
        responseLine = new ResponseLine(status, protocol);
    }

    public void addCookie(HttpCookie cookie) {
        cookies.add(cookie);
        headers.add(COOKIE.value(), cookie.value());
    }
}
