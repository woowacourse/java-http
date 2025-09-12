package org.apache.coyote.http11;

import jakarta.servlet.ServletException;
import org.apache.catalina.handler.Controller;
import org.apache.catalina.handler.LoginHandler;
import org.apache.catalina.handler.RegisterHandler;
import org.apache.catalina.handler.StaticHandler;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.file.NoSuchFileException;
import java.util.*;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
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
            final Http11Request request = readRequest(inputStream);
            final Http11Response response = new Http11Response();

            handle(request, response);

            final byte[] responseMessage = response.toMessage();

            outputStream.write(responseMessage);
            outputStream.flush();
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                connection.close();
            } catch (final IOException e) {
                log.error("failed to close connection : ", e);
            }
        }
    }

    private Http11Request readRequest(final InputStream requestInputStream) throws IOException {
        final InputStreamReader inputStreamReader = new InputStreamReader(requestInputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        final List<String> headerLines = getHeaderLines(bufferedReader);
        final int contentLength = getContentLength(headerLines);
        final char[] bodyChars = getBody(contentLength, bufferedReader);

        final String rawHttpRequest = String.join("\r\n", headerLines)
                + "\r\n\r\n"
                + new String(bodyChars);
        return Http11Request.create(rawHttpRequest);
    }

    private void handle(final Http11Request request, final Http11Response response) {
        try {
            final Controller controller = mapHandler(request);
            controller.service(request, response);
        } catch (final IllegalArgumentException e) {
            log.warn("bad request : {}", e.getMessage());
            response.setRedirectResponse("/400.html");
        } catch (final ServletException e) {
            log.warn("unauthorized : {}", e.getMessage());
            response.setRedirectResponse("/401.html");
        } catch (final NoSuchFileException e) {
            log.warn("not found : {}", e.getFile());
            response.setRedirectResponse("/404.html");
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            response.setRedirectResponse("/500.html");
        }
    }

    private Controller mapHandler(final Http11Request request) throws Exception {
        final String requestTarget = request.getPath();

        if (requestTarget.endsWith("/login")) {
            return new LoginHandler();
        }
        if (requestTarget.endsWith("/register")) {
            return new RegisterHandler();
        }
        if (requestTarget.equals("/") || HttpContentType.isStaticResource(requestTarget)) {
            return new StaticHandler();
        }
        throw new NoSuchFileException(requestTarget);
    }

    private List<String> getHeaderLines(final BufferedReader bufferedReader) throws IOException {
        final List<String> headerLines = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            headerLines.add(line);
        }
        return headerLines;
    }

    private int getContentLength(final List<String> headerLines) {
        return headerLines.stream()
                .filter(l -> l.startsWith("Content-Length:"))
                .map(l -> Integer.parseInt(l.split(":")[1].trim()))
                .findFirst()
                .orElse(0);
    }

    private char[] getBody(final int contentLength, final BufferedReader bufferedReader) throws IOException {
        final char[] bodyChars = new char[contentLength];
        if (contentLength > 0) {
            bufferedReader.read(bodyChars, 0, contentLength);
        }
        return bodyChars;
    }
}
