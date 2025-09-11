package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.parser.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final SessionManager sessionManager;

    public Http11Processor(final Socket connection, SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.connection = connection;
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

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequests httpRequests = createHttpRequests(bufferedReader);

            HttpController httpController = new HttpController(httpRequests);
            HttpResponse httpResponse = httpController.doRequest();

            System.out.println(httpResponse.getResult());

            outputStream.write(httpResponse.getResult()
                    .getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequests createHttpRequests(BufferedReader bufferedReader) throws IOException {
        HttpInfo httpInfo = new HttpInfo(bufferedReader.readLine());
        HttpHeader httpHeader = readHeaders(bufferedReader);
        HttpBody httpBody = readBodies(bufferedReader, httpHeader);
        HttpCookies httpCookies = new HttpCookies(httpHeader.getCookie());
        Session session = getSession(httpCookies.getSessionId());

        return new HttpRequests(httpInfo, httpHeader, httpBody, httpCookies, session);
    }

    private HttpBody readBodies(BufferedReader bufferedReader, HttpHeader httpHeader) throws IOException {
        String contentLengthHeader = httpHeader.getHeaderMap()
                .getOrDefault("Content-Length", "0");
        int contentLength = Integer.parseInt(contentLengthHeader);

        char[] contents = new char[contentLength];
        bufferedReader.read(contents, 0, contentLength);

        String requestBody = new String(contents);
        return new HttpBody(requestBody);
    }

    private HttpHeader readHeaders(BufferedReader bufferedReader) throws IOException {
        List<String> headers = new ArrayList<>();
        String buffer;
        while (!(buffer = bufferedReader.readLine()).isEmpty()) {
            headers.add(buffer);
        }

        return new HttpHeader(headers);
    }

    private Session getSession(String sessionId) throws IOException {
        Session session = sessionManager.findSession(sessionId);
        if (session == null) {
            sessionManager.add(new Session(sessionId));
            return sessionManager.findSession(sessionId);
        }
        return session;
    }
}
