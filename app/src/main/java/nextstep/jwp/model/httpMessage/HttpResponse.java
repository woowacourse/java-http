package nextstep.jwp.model.httpMessage;

import nextstep.jwp.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

import static nextstep.jwp.model.httpMessage.HttpHeaderType.CONTENT_TYPE;
import static nextstep.jwp.model.httpMessage.HttpHeaderType.LOCATION;
import static nextstep.jwp.model.httpMessage.HttpProtocol.OK;
import static nextstep.jwp.model.httpMessage.HttpProtocol.REDIRECT;
import static nextstep.jwp.model.httpMessage.MediaType.HTML;

public class HttpResponse {

    private static final Logger LOG = LoggerFactory.getLogger(HttpResponse.class);
    private static final String DELIMITER = "\r\n";

    private final OutputStream outputStream;
    private final HttpHeaders headers;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.headers = new HttpHeaders();
    }

    public void forward(String url) throws IOException {
        String body = FileUtils.getAllResponseBodies(url);
        LOG.debug("Response header : url : {}", url);

        headers.addProtocol(OK);
        LOG.debug("Response header : protocol : {}", headers.getProtocol(OK));

        MediaType.of(url).ifPresent(type -> headers.add(CONTENT_TYPE, type.value()));

        headers.setContentLength(body.getBytes(StandardCharsets.UTF_8).length);
        LOG.debug("Response header : content length : {}", body.getBytes(StandardCharsets.UTF_8).length);

        writeBody(outputStream, processAllHeaders(body));
    }

    public void forwardBody(String body) throws IOException {
        headers.addProtocol(OK);
        headers.add(CONTENT_TYPE, HTML.value());

        String response = processAllHeaders(body);
        writeBody(outputStream, response);
    }

    public void redirect(String path) throws IOException {
        headers.addProtocol(REDIRECT);
        headers.add(LOCATION, path);

        String response = processAllHeaders();
        writeBody(outputStream, response);
    }

    private String processAllHeaders(String... body) {
        StringJoiner stringJoiner = new StringJoiner(DELIMITER);
        stringJoiner.add(headers.getAllHeaders());
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

    public void addProtocol(HttpProtocol httpProtocol) {
        headers.addProtocol(httpProtocol);
    }

    public void addHeader(HttpHeaderType header, String value) {
        headers.add(header, value);
    }
}
