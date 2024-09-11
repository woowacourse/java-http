package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.RequestMapper;
import org.apache.coyote.Processor;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.RequestBody;
import org.apache.coyote.request.RequestLine;
import org.apache.coyote.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapper requestMapper;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.requestMapper = new RequestMapper();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream()) {
            HttpResponse response = new HttpResponse();
            handle(readRequest(new BufferedReader(new InputStreamReader(inputStream))), response);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest readRequest(BufferedReader reader) throws IOException {
        return new HttpRequest(
                new RequestLine(reader.readLine()),
                new HttpHeader(readRequestHeaders(reader)),
                readRequestBody(reader, new HttpHeader(readRequestHeaders(reader)))
        );
    }

    private List<String> readRequestHeaders(BufferedReader reader) throws IOException {
        List<String> rawHeaders = new ArrayList<>();

        while (reader.ready()) {
            String line = reader.readLine();
            if (line.isBlank()) {
                break;
            }
            rawHeaders.add(line);
        }

        return rawHeaders;
    }

    private RequestBody readRequestBody(BufferedReader reader, HttpHeader httpHeader) throws IOException {
        if (httpHeader.contains(HttpHeaderType.CONTENT_LENGTH.getName())) {
            int contentLength = Integer.parseInt(httpHeader.get(HttpHeaderType.CONTENT_LENGTH.getName()));
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            return new RequestBody(new String(buffer));
        }
        return null;
    }

    private void handle(HttpRequest request, HttpResponse response) {
        Controller controller = requestMapper.getController(request);
        controller.service(request, response);
    }
}
