package nextstep.jwp.model.httpmessage.response;

import nextstep.jwp.model.httpmessage.common.ContentType;
import nextstep.jwp.model.httpmessage.common.HttpHeaderType;
import nextstep.jwp.util.FileUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

import static nextstep.jwp.model.httpmessage.common.CommonHttpHeader.DELIMITER;
import static nextstep.jwp.model.httpmessage.common.ContentType.HTML;
import static nextstep.jwp.model.httpmessage.common.HttpHeaderType.CONTENT_TYPE;
import static nextstep.jwp.model.httpmessage.response.HttpStatus.OK;
import static nextstep.jwp.model.httpmessage.response.HttpStatus.REDIRECT;
import static nextstep.jwp.model.httpmessage.response.ResponseHeaderType.LOCATION;

public class HttpResponse {

    public static final String EMPTY_LINE = "";

    private final OutputStream outputStream;
    private final ResponseHeader headers = new ResponseHeader();
    private ResponseLine responseLine;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void forward(String url) throws IOException {
        responseLine = new ResponseLine(OK);
        ContentType.of(url).ifPresent(type -> headers.add(CONTENT_TYPE, type.value()));
        String body = FileUtils.readFileOfUrl(url);
        headers.setContentLength(body.getBytes(StandardCharsets.UTF_8).length);
        writeBody(outputStream, processResponseLineAndHeader(body));
    }

    public void forwardBody(String body) throws IOException {
        responseLine = new ResponseLine(OK);
        headers.add(CONTENT_TYPE, HTML.value());
        headers.setContentLength(body.getBytes(StandardCharsets.UTF_8).length);
        writeBody(outputStream, processResponseLineAndHeader(body));
    }

    public void redirect(String path) throws IOException {
        responseLine = new ResponseLine(REDIRECT);
        headers.add(LOCATION, path);
        writeBody(outputStream, processResponseLineAndHeader());
    }

    private String processResponseLineAndHeader(String... body) {
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

    public void setStatus(HttpStatus httpStatus) {
        responseLine = new ResponseLine(httpStatus);
    }

    public void addHeader(HttpHeaderType type, String value) {
        headers.add(type, value);
    }
}
