package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import nextstep.jwp.Controller;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.model.ContentType;
import org.apache.coyote.http11.model.Header;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.request.Method;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.response.Resource;
import org.apache.coyote.http11.model.response.Status;
import org.apache.coyote.http11.utils.ResourceMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            HttpRequest request = readHttpRequest(reader);
            HttpResponse response = Controller.process(request);

            if (response.hasLocation()) {
                String location = response.getHeaderValue(Header.LOCATION);
                response.addResource(findResource(location));
            } else if (response.getStatus() == Status.UNAUTHORIZED) {
                response.addResource(findResource("/401.html"));
            } else {
                response.addResource(findResource(request.getUrl()));
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest readHttpRequest(final BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        List<String> headerLines = readHeaders(reader);
        HttpRequest request = HttpRequest.from(requestLine, headerLines);

        if (request.getMethod() == Method.POST) {
            int contentLength = Integer.parseInt(request.getHeaderValue(Header.CONTENT_LENGTH));
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            String requestBody = new String(buffer);
            request.addBody(requestBody);
        }
        return request;
    }

    private List<String> readHeaders(final BufferedReader reader) throws IOException {
        List<String> headerLines = new ArrayList<>();
        while (true) {
            String line = reader.readLine();
            if (line.isEmpty()) {
                break;
            }
            headerLines.add(line);
        }
        return headerLines;
    }

    private Resource findResource(final String url) throws IOException {
        String fileName = ResourceMatcher.matchName(url);
        Path path = Path.of(Objects.requireNonNull(this.getClass().getResource("/static" + fileName)).getPath());
        String body = Files.readString(path);

        ContentType contentType = ContentType.findByExtension(url);

        return new Resource(body, contentType);
    }
}
