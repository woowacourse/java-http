package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.dispatcher.RequestMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.file.RequestFactory;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.file.ResponseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UncheckedServletException;

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
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            final RequestMapping requestMapping = new RequestMapping();

            final HttpRequest request = createRequest(bufferedReader);
            final Controller controller = requestMapping.getController(request);
            final HttpResponse response = new HttpResponse();

            controller.service(request, response);

            outputStream.write(createRawResponse(response).getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private HttpRequest createRequest(BufferedReader bufferedReader) throws IOException {
        final String requestLine = RequestFactory.readRequestLine(bufferedReader);
        final String requestHeaders = RequestFactory.readHeaders(bufferedReader);
        final HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders);

        int contentLength = httpRequest.getContentLength();
        final String requestBody = RequestFactory.readBody(bufferedReader, contentLength);
        httpRequest.setBody(requestBody);

        return httpRequest;
    }

    private String createRawResponse(HttpResponse response) {
        return ResponseFactory.writeResponse(response);
    }
}
