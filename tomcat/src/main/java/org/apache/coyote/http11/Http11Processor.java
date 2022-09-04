package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.Processor;
import org.apache.coyote.servlet.Servlet;
import org.apache.coyote.servlet.request.HttpRequest;
import org.apache.coyote.servlet.request.RequestHeaders;
import org.apache.coyote.servlet.request.StartLine;
import org.apache.coyote.servlet.response.HttpResponse;
import org.apache.coyote.servlet.session.Session;
import org.apache.coyote.servlet.session.SessionRepository;
import org.apache.coyote.support.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Servlet servlet;
    private final SessionRepository sessionRepository;

    public Http11Processor(final Socket connection, final Servlet servlet, final SessionRepository sessionRepository) {
        this.connection = connection;
        this.servlet = servlet;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             final var reader = new BufferedReader(streamReader);
             final var outputStream = connection.getOutputStream()) {

            HttpRequest request = toRequest(reader);
            HttpResponse response = servlet.service(request);

            outputStream.write(response.toMessage().getBytes());
            outputStream.flush();
        } catch (IOException | HttpException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest toRequest(BufferedReader reader) throws IOException {
        final var startLine = StartLine.of(reader.readLine());
        final var headers = readHeaders(reader);
        final var body = readBody(reader, headers);
        return new HttpRequest(startLine, headers, body, getCurrentSession(headers));
    }

    private RequestHeaders readHeaders(BufferedReader reader) throws IOException {
        List<String> request = new ArrayList<>();
        String line;
        while ((line = reader.readLine()).length() > 0) {
            request.add(line);
        }
        return RequestHeaders.of(request);
    }

    private String readBody(BufferedReader reader, RequestHeaders headers) throws IOException {
        int contentLength = headers.getContentLength();
        if (contentLength == 0) {
            return "";
        }
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private Session getCurrentSession(RequestHeaders headers) {
        final var sessionCookie = headers.getSessionCookie();
        if (!sessionRepository.isValidSessionCookie(sessionCookie)) {
            final var session = Session.of();
            sessionRepository.add(session);
            return session;
        }
        return sessionRepository.findSession(sessionCookie.getValue());
    }
}
