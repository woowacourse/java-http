package org.apache.coyote.http11;

import com.techcourse.api.Controller;
import com.techcourse.api.RequestMapping;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Manager sessionManager;
    private final RequestMapping requestMapping;

    public Http11Processor(
            final Socket connection,
            final Manager sessionManager,
            final RequestMapping requestMapping
    ) {
        this.connection = connection;
        this.sessionManager = sessionManager;
        this.requestMapping = requestMapping;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (
                final var inputStream = new BufferedInputStream(connection.getInputStream());
                final var outputStream = connection.getOutputStream()
        ) {
            final HttpRequest request = readHttpRequest(inputStream);
            final HttpResponse response = new HttpResponse();

            service(request, response);
            addSessionCookieIfNecessary(request, response);

            outputStream.write(response.getResponse());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void service(final HttpRequest request, final HttpResponse response) throws Exception {
        final Controller controller = requestMapping.getController(request.getMethod(), request.getPath());
        if (controller != null) {
            controller.service(request, response);
            return;
        }

        if (request.isGet()) {
            response.setStatus(HttpStatus.OK);
            response.setContentType(ContentType.fromResourceName(request.getPath()));
            response.setBody(HttpResponse.resolveResource(request.getPath()));
            return;
        }

        throw new UnsupportedOperationException("지원하지 않는 요청입니다: " + request.getMethod() + " " + request.getPath());
    }

    private void addSessionCookieIfNecessary(final HttpRequest request, final HttpResponse response) {
        if (request.getSessionId() != null || response.containsHeader("Set-Cookie")) {
            return;
        }

        // TODO: 세션 생성 책임을 processor로부터 분리
        final Session session = new Session(UUID.randomUUID().toString());
        sessionManager.add(session);
        response.setHeader("Set-Cookie", "JSESSIONID=" + session.getId());
    }

    private static HttpRequest readHttpRequest(final BufferedInputStream inputStream) throws IOException {
        final RequestLine requestLine = readRequestLine(inputStream);
        final Headers headers = readHeaders(inputStream);
        final RequestBody requestBody = readRequestBody(inputStream, headers.contentLength());

        return new HttpRequest(requestLine, headers, requestBody);
    }

    private static RequestLine readRequestLine(final BufferedInputStream inputStream) throws IOException {
        return new RequestLine(readLine(inputStream));
    }

    private static Headers readHeaders(final BufferedInputStream inputStream) throws IOException {
        final Headers headers = new Headers();

        String line = readLine(inputStream);
        while (!"".equals(line)) {
            if (line == null) {
                throw new IllegalArgumentException("헤더가 올바르지 않습니다.");
            }
            headers.add(line);
            line = readLine(inputStream);
        }
        return headers;
    }

    private static RequestBody readRequestBody(
            final BufferedInputStream inputStream,
            final int contentLength
    ) throws IOException {
        final byte[] body = inputStream.readNBytes(contentLength);
        return new RequestBody(new String(body, StandardCharsets.UTF_8));
    }

    private static String readLine(final BufferedInputStream inputStream) throws IOException {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int value;
        while ((value = inputStream.read()) != -1 && value != '\n') {
            buffer.write(value);
        }
        if (value == -1 && buffer.size() == 0) {
            return null;
        }

        final byte[] bytes = buffer.toByteArray();
        final int length = (bytes.length > 0) && (bytes[bytes.length - 1] == '\r')
                ? bytes.length - 1
                : bytes.length;
        return new String(bytes, 0, length, StandardCharsets.UTF_8);
    }
}
