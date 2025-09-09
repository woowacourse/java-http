package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.HttpRequestHandler;
import org.apache.coyote.http11.handler.HttpRequestHandlerContainer;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final HttpRequestHandlerContainer handlerContainer = new HttpRequestHandlerContainer();

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
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream()
        ) {
            final String request = parseRequest(inputStream);
            final HttpResponse response = processResponse(request);

            outputStream.write(response.toHttpResponse().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    public String parseRequest(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuilder fullRequest = new StringBuilder();

        String line;
        int contentLength = 0;

        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            fullRequest.append(line).append(System.lineSeparator());
            if (line.startsWith("Content-Length")) {
                contentLength = Integer.parseInt(line.substring(line.indexOf(":") + 1).trim());
            }
        }

        fullRequest.append(System.lineSeparator());

        if (contentLength > 0) {
            char[] body = new char[contentLength];
            int bytesRead = bufferedReader.read(body, 0, contentLength);
            if (bytesRead != -1) {
                fullRequest.append(body, 0, bytesRead);
            }
        }

        return fullRequest.toString();
    }

    private HttpResponse processResponse(String request) {
        String url = getUrl(request);
        HttpRequestHandler httpRequestHandler = handlerContainer.getHandler(url);
        if (httpRequestHandler == null) {
            throw new IllegalArgumentException("No resource found: " + url);
        }
        return httpRequestHandler.handle(request);
    }

    private String getUrl(String request) {
        return request.split(System.lineSeparator())[0].split(" ")[1].split("\\?")[0];
    }
}
