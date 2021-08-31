package nextstep.jwp.model.httpmessage.response;

import nextstep.jwp.model.httpmessage.common.HttpHeader;

import java.io.IOException;
import java.io.OutputStream;
import java.util.StringJoiner;

import static nextstep.jwp.model.httpmessage.common.CommonHttpHeader.DELIMITER;
import static nextstep.jwp.model.httpmessage.response.HttpStatus.REDIRECT;
import static nextstep.jwp.model.httpmessage.response.ResponseHeaderType.LOCATION;

public class HttpResponse {

    public static final String EMPTY_LINE = "";

    private final OutputStream outputStream;
    private final ResponseHeader headers = new ResponseHeader();
    private ResponseLine responseLine;
    private String body;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void redirect(String path) throws IOException {
        setStatus(REDIRECT);
        headers.add(LOCATION, path);
        writeBody(outputStream, processResponseLineAndHeader());
    }

    public String processResponseLineAndHeader(String... body) {
        StringJoiner stringJoiner = new StringJoiner(DELIMITER);
        stringJoiner.add(responseLine.toString());
        stringJoiner.add(headers.toString());
        stringJoiner.add(EMPTY_LINE);
        if (body.length > 0) {
            stringJoiner.add(body[0]);
        }
        return stringJoiner.toString();
    }

    private void writeBody(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    public HttpHeader getHeaders() {
        return headers;
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
