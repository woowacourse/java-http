package org.apache.coyote.http11;

import com.techcourse.controller.Controller;
import com.techcourse.controller.ControllerResolver;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String SET_COOKIE = "Set-Cookie";
    public static final String CRLF = "\r\n";

    private final Socket connection;
    private final SessionManager sessionManager;
    private final ControllerResolver controllerResolver;

    public Http11Processor(final Socket connection, final SessionManager sessionManager,
                           final ControllerResolver controllerResolver) {
        this.connection = connection;
        this.sessionManager = sessionManager;
        this.controllerResolver = controllerResolver;
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

            final BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String requestHead = getRequestHead(bufferedReader);

            HttpRequest request = HttpRequest.parse(requestHead);
            String requestBody = getRequestBody(bufferedReader, request.getContentLength());
            request.parseBody(requestBody);

            String requestUri = request.getRequestTarget();

            HttpResponse response = HttpResponse.create();
            response.addHeader(CONTENT_TYPE_HEADER, getContentType(requestUri));
            Session session = getOrCreateJSessionId(request.getCookie(), response);
            request.setSession(session);

            Controller controller = controllerResolver.resolve(request.getRequestTarget());
            controller.service(request, response);

            outputStream.write(response.getResponse().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();

        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private String getRequestBody(BufferedReader bufferedReader, Integer contentLength) throws IOException {
        if (contentLength == null || contentLength == 0) {
            return "";
        }

        char[] buffer = new char[contentLength];
        int readCount = bufferedReader.read(buffer, 0, contentLength);

        if (readCount == -1) {
            throw new IOException("요청 본문을 읽지 못했습니다.");
        }
        return new String(buffer, 0, readCount);
    }

    private String getRequestHead(BufferedReader bufferedReader) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();

        String line = bufferedReader.readLine();
        while (line != null && !line.isBlank()) {
            stringBuilder.append(line).append(CRLF);
            line = bufferedReader.readLine();
        }

        return stringBuilder.toString();
    }

    private Session getOrCreateJSessionId(HttpCookie cookie, HttpResponse response) throws IOException {
        if (!cookie.contains("JSESSIONID")) {
            return createSession(response);
        }

        String jSessionId = cookie.getJSessionId();
        Session session = sessionManager.findSession(jSessionId);

        if (session == null) {
            return createSession(response);
        }

        return session;
    }

    private Session createSession(HttpResponse response) {
        Session session = sessionManager.createSession();
        response.addHeader(SET_COOKIE, "JSESSIONID=" + session.getId() + ";");
        return session;
    }

    private String getContentType(String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "text/css;charset=utf-8 ";
        }
        return "text/html;charset=utf-8 ";
    }
}
