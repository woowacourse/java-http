package nextstep.jwp.http.response;

import java.io.OutputStream;
import nextstep.jwp.exception.http.InvalidHttpResponseException;

public class HttpResponse {

    private final ResponseHeader header;
    private ResponseBody body;

    public HttpResponse() {
        this(new ResponseHeader());
    }

    private HttpResponse(ResponseHeader header) {
        this.header = header;
    }

    public void setContentType(String contentType) {
        header.setContentType(contentType);
    }

    public void setLine(HttpStatus httpStatus) {
        header.setLine(httpStatus);
    }

    public void setBody(ResponseBody body) {
        this.body = body;
        header.setContentLength(body.getLength());
    }

    public void write(OutputStream outputStream) {
        try {
            outputStream.write(getResponse().getBytes());
            outputStream.flush();
        } catch (Exception e) {
            throw new InvalidHttpResponseException();
        }
    }

    /**
     * HTTP/1.1 200 OK
     * "Content-Type: text/html;charset=utf-8 ",
     * "Content-Length: " + responseBody.getLength() + " ",
     * "",
     * responseBody.getBody());
     */
    public String getResponse() {
        StringBuilder response = new StringBuilder();

        ResponseLine line = header.getLine();
        response.append(line.getProtocol())
            .append(" ")
            .append(line.getCode())
            .append(" ")
            .append(line.getMessage())
            .append(" ")
            .append("\r\n");

        for (String key : header.getKeySet()) {
            response.append(key)
                .append(": ")
                .append(header.getValue(key))
                .append(" ")
                .append("\r\n");
        }

        if (body != null) {
            response.append("\r\n")
                .append(body.getBody());
        }

        return response.toString();
    }

    public ResponseHeader getHeader() {
        return header;
    }

    public ResponseBody getBody() {
        return body;
    }
}
