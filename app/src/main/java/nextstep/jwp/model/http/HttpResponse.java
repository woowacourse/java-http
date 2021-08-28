package nextstep.jwp.model.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static nextstep.jwp.model.http.ContentType.*;
import static nextstep.jwp.model.http.HTTPHeaders.*;
import static nextstep.jwp.model.http.HTTPProtocol.OK;
import static nextstep.jwp.model.http.HTTPProtocol.REDIRECT;

public class HttpResponse {

    private static final Logger LOG = LoggerFactory.getLogger(HttpResponse.class);
    private static final String DELIMITER = "\r\n";

    private final OutputStream outputStream;
    private final Map<HTTPHeaders, String> headers = new LinkedHashMap<>();

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void forward(String url) throws IOException {
        String body = getAllResponseBodies(url);
        LOG.debug("Response header : url : {}", url);

        headers.put(PROTOCOL, OK.getProtocol());
        findContentTypeByUrlSuffix(url);
        headers.put(CONTENT_LENGTH, String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));

        writeBody(outputStream, responseHeadersWithBody(body));
    }

    private void findContentTypeByUrlSuffix(String url) {
        ContentType.of(url)
                .ifPresent(type -> headers.put(CONTENT_TYPE, type.value()));
    }

    public void forwardBody(String body) throws IOException {
        headers.put(PROTOCOL, OK.getProtocol());
        headers.put(CONTENT_TYPE, HTML.value());

        String response = responseHeadersWithBody(body);
        writeBody(outputStream, response);
    }

    public void forwardWithBody(String path) throws IOException {
        headers.put(PROTOCOL, OK.getProtocol());
        headers.put(CONTENT_TYPE, HTML.value());

        String body = getAllResponseBodies(path);
        String response = responseHeadersWithBody(body);
        writeBody(outputStream, response);
    }

    public void redirect(String path) throws IOException {
        headers.put(PROTOCOL, REDIRECT.getProtocol());
        headers.put(LOCATION, path);

        String response = responseHeaders();
        writeBody(outputStream, response);
    }

    private String getAllResponseBodies(String url) throws IOException {
        URL resource = getClass().getClassLoader().getResource("static" + url);
        String file = Objects.requireNonNull(resource).getFile();
        Path path = new File(file).toPath();
        return new String(Files.readAllBytes(path));
    }

    private String responseHeaders() {
        StringJoiner stringJoiner = processAllHeaders();
        return stringJoiner.toString();
    }

    private String responseHeadersWithBody(String body) {
        StringJoiner stringJoiner = processAllHeaders();
        stringJoiner.add(body);
        return stringJoiner.toString();
    }

    private StringJoiner processAllHeaders() {
        StringJoiner stringJoiner = new StringJoiner(DELIMITER);

        for (Map.Entry<HTTPHeaders, String> entry : headers.entrySet()) {
            stringJoiner.add(entry.getKey().value() + entry.getValue() + " ");
        }

        stringJoiner.add("");
        return stringJoiner;
    }

    private void writeBody(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    public void addProtocol(HTTPProtocol httpProtocol) {
        headers.put(PROTOCOL, httpProtocol.getProtocol());
    }

    public void addHeader(HTTPHeaders header, String value) {
        headers.put(header, value);
    }
}
