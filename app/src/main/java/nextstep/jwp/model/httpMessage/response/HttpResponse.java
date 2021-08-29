package nextstep.jwp.model.httpMessage.response;

import nextstep.jwp.model.httpMessage.ContentType;
import nextstep.jwp.model.httpMessage.HttpHeaderType;
import nextstep.jwp.model.httpMessage.HttpStatus;
import nextstep.jwp.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

import static nextstep.jwp.model.httpMessage.CommonHttpHeader.DELIMITER;
import static nextstep.jwp.model.httpMessage.HttpHeaderType.CONTENT_TYPE;
import static nextstep.jwp.model.httpMessage.HttpHeaderType.LOCATION;
import static nextstep.jwp.model.httpMessage.HttpStatus.OK;
import static nextstep.jwp.model.httpMessage.HttpStatus.REDIRECT;
import static nextstep.jwp.model.httpMessage.ContentType.HTML;

public class HttpResponse {

    private static final Logger LOG = LoggerFactory.getLogger(HttpResponse.class);

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
        writeBody(outputStream, processAllHeaders(body));
    }

    public void forwardBody(String body) throws IOException {
        responseLine = new ResponseLine(OK);
        headers.add(CONTENT_TYPE, HTML.value());
        headers.setContentLength(body.getBytes(StandardCharsets.UTF_8).length);
        String response = processAllHeaders(body);
        writeBody(outputStream, response);
    }

    public void redirect(String path) throws IOException {
        responseLine = new ResponseLine(REDIRECT);
        headers.add(LOCATION, path);
        String response = processAllHeaders();
        writeBody(outputStream, response);
    }

    private String processAllHeaders(String... body) {
        StringJoiner stringJoiner = new StringJoiner(DELIMITER);
        stringJoiner.add(responseLine.toString());
        stringJoiner.add(headers.toString());
        stringJoiner.add("");
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
