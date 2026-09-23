package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.Processor;
import org.apache.coyote.Adapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final Socket connection;
    private final Adapter adapter;
    private final SessionManager sessionManager;

    public Http11Processor(final Socket connection, final Adapter adapter, final SessionManager sessionManager) {
        this.connection = connection;
        this.adapter = adapter;
        this.sessionManager = sessionManager;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (connection;
             final var inputStream = new BufferedInputStream(connection.getInputStream());
             final var outputStream = connection.getOutputStream()) {

            Optional<HttpResponse> response = handleRequest(inputStream);
            if (response.isEmpty()) {
                return;
            }

            writeResponse(outputStream, response.get());
        } catch (SocketTimeoutException e) {
            log.warn("Request read timed out: {}:{}", connection.getInetAddress(), connection.getPort());
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Optional<HttpResponse> handleRequest(InputStream inputStream) throws IOException {
        try {
            Optional<HttpRequest> request = readRequest(inputStream);
            if (request.isEmpty()) {
                return Optional.empty();
            }

            String sessionId = request.get().cookies().getCookie("JSESSIONID");
            Session session = sessionManager.findSession(sessionId);
            boolean created = session == null || !session.isValid();

            if (created) {
                if (session != null) {
                    sessionManager.remove(session);
                }
                session = new Session(UUID.randomUUID().toString());
                sessionManager.add(session);
            }

            HttpResponse response = adapter.service(request.get(), session);

            if (created) {
                response.setCookie("JSESSIONID", session.getId());
            }

            return Optional.of(response);
        } catch (BadRequestException e) {
            return Optional.of(HttpResponse.badRequest(
                    "400 Bad Request".getBytes(StandardCharsets.UTF_8)
            ));
        }
    }

    private Optional<HttpRequest> readRequest(InputStream inputStream) throws IOException {
        String requestLine = readline(inputStream);
        if (requestLine == null) {
            return Optional.empty();
        }

        Map<String, String> headers = HttpRequest.parseHeaders(readHeaderLines(inputStream));
        byte[] body = readBody(inputStream, headers);
        return Optional.of(HttpRequest.parse(requestLine, headers, body));
    }

    private void writeResponse(OutputStream outputStream, HttpResponse response) throws IOException {
        outputStream.write(response.toBytes());
        outputStream.flush();
    }

    private String readline(InputStream inputStream) throws IOException {
        ByteArrayOutputStream line = new ByteArrayOutputStream();

        while (true) {
            int value = inputStream.read();

            if (value == -1) {
                if (line.size() == 0) {
                    return null;
                }
                throw new BadRequestException("Incomplete HTTP line");
            }

            if (value == '\r') {
                if (inputStream.read() != '\n') {
                    throw new BadRequestException("Invalid line ending");
                }
                return line.toString(StandardCharsets.ISO_8859_1);
            }

            if (value == '\n') {
                throw new BadRequestException("Invalid line ending");
            }

            line.write(value);
        }
    }

    private List<String> readHeaderLines(InputStream inputStream) throws IOException {
        List<String> headerLines = new ArrayList<>();
        String line;
        while ((line = readline(inputStream)) != null && !line.isBlank()) {
            headerLines.add(line);
        }
        return headerLines;
    }

    private byte[] readBody(InputStream inputStream, Map<String, String> headers) throws IOException {
        if (headers.containsKey("content-length")) {
            int contentLength = parseContentLength(headers.get("content-length"));
            return inputStream.readNBytes(contentLength);
        }
        return new byte[0];
    }

    private int parseContentLength(String value) {
        if (!value.matches("[0-9]+")) {
            throw new BadRequestException("Invalid Content-Length");
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Content-Length is out of range");
        }
    }
}
