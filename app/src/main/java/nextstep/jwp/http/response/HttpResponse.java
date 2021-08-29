package nextstep.jwp.http.response;

import static nextstep.jwp.http.response.ContentType.HTML;
import static nextstep.jwp.http.response.ContentType.from;

import java.io.OutputStream;
import nextstep.jwp.exception.http.response.InvalidHttpResponseException;

public class HttpResponse {

    private static final String EXTENSION_MARK = ".";

    private final ResponseHeader header;
    private ResponseBody body;

    public HttpResponse() {
        this(new ResponseHeader());
    }

    private HttpResponse(ResponseHeader header) {
        this.header = header;
    }

    public void forward(HttpStatus httpStatus, String uri) {
        setLine(httpStatus);
        setContentType(uri);
    }

    private void setLine(HttpStatus httpStatus) {
        header.setLine(httpStatus);
    }

    private void setContentType(String uri) {
        if (uri.contains(EXTENSION_MARK)) {
            header.setContentType(from(uri).getValue());
            return;
        }
        header.setContentType(HTML.getValue());
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
