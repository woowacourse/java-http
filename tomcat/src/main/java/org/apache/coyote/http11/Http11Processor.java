package org.apache.coyote.http11;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.pageController.RequestDispatcher;
import org.apache.coyote.http11.request.HttpBody;
import org.apache.coyote.http11.request.HttpCookie;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Http11Processor implements Runnable, Processor {
    private static final Set<HttpMethod> SUPPORTED_METHODS = Set.of(HttpMethod.GET, HttpMethod.POST);

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final SessionManager sessionManager;
    private final RequestDispatcher requestDispatcher;

    public Http11Processor(
            final Socket connection,
            final SessionManager sessionManager,
            final RequestDispatcher requestDispatcher
    ) {
        this.connection = connection;
        this.sessionManager = sessionManager;
        this.requestDispatcher = requestDispatcher;
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
            HttpResponse response = createResponse(inputStream);

            writeResponse(outputStream, response);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse createResponse(InputStream inputStream) throws IOException {
        try {
            HttpRequest request = getRequestTarget(inputStream);

            HttpResponse response = requestDispatcher.dispatch(request);
            issueSessionCookie(request, response);

            return response;
        } catch (BadRequestException e) {
            return badRequest(e);
        }
    }

    private void issueSessionCookie(HttpRequest request, HttpResponse response) {
        if (request.getSession(false) != null) {
            return;
        }

        Session session = request.getSession(true);
        response.addCookie(HttpCookie.JSESSIONID, session.getId());
    }

    private HttpResponse badRequest(BadRequestException e) {
        log.warn(e.getMessage());
        return HttpResponse.of(HttpStatus.BAD_REQUEST, "text/plain", e.getMessage());
    }

    private void writeResponse(OutputStream outputStream, HttpResponse response) throws IOException {
        outputStream.write(response.toBytes());
        outputStream.flush();
    }

    private HttpRequest getRequestTarget(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        String requestLine = reader.readLine();
        if (requestLine == null) {
            throw new IOException("HTTP 요청 라인이 존재하지 않습니다.");
        }

        HttpHeaders headers = readHeaders(reader);
        HttpBody body = readBody(reader, headers.getContentLength());

        return HttpRequest.from(requestLine, headers, body, SUPPORTED_METHODS, sessionManager);
    }

    private HttpHeaders readHeaders(BufferedReader reader) throws IOException {
        List<String> headerLines = new ArrayList<>();

        String line = reader.readLine();
        while (line != null && !line.isEmpty()) {
            headerLines.add(line);
            line = reader.readLine();
        }

        if (line == null) {
            throw new IOException("HTTP 헤더가 빈 줄로 끝나지 않았습니다.");
        }

        return HttpHeaders.from(headerLines);
    }

    private HttpBody readBody(BufferedReader reader, int contentLength) throws IOException {
        if (contentLength == 0) {
            return HttpBody.empty();
        }

        char[] buffer = new char[contentLength];
        int totalRead = 0;
        while (totalRead < contentLength) {
            int read = reader.read(buffer, totalRead, contentLength - totalRead);
            if (read == -1) {
                throw new IOException("HTTP body가 Content-Length보다 짧습니다.");
            }
            totalRead += read;
        }

        return new HttpBody(new String(buffer));
    }
}
