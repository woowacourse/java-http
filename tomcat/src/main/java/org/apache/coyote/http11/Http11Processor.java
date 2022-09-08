package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.exception.NoSuchUserException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.ControllerContainer;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String CONTENT_LENGTH = "Content-Length";

    private final Socket connection;
    private final ControllerContainer controllerContainer;

    public Http11Processor(final Socket connection, final ControllerContainer controllerContainer) {
        this.connection = connection;
        this.controllerContainer = controllerContainer;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final BufferedReader bufferedReader
                     = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             final OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = makeHttpRequest(bufferedReader);
            HttpResponse httpResponse = new HttpResponse();

            Controller controller = controllerContainer.find(httpRequest);

            try {
                controller.service(httpRequest, httpResponse);
            } catch (NoSuchUserException e) {
                httpResponse.redirect("/401.html");
            } catch (Exception e) {
                httpResponse.redirect("/404.html");
            }

            outputStream.write(httpResponse.getValue().getBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest makeHttpRequest(final BufferedReader bufferedReader) throws IOException {
        RequestLine requestLine = RequestLine.of(bufferedReader.readLine());
        Headers requestHeaders = Headers.of(readRequestHeaders(bufferedReader));
        String body = readRequestBody(bufferedReader, requestHeaders);
        return new HttpRequest(requestLine, requestHeaders, body);
    }

    private List<String> readRequestHeaders(final BufferedReader bufferedReader) throws IOException {
        final List<String> request = new ArrayList<>();
        String line;
        while (!(line = bufferedReader.readLine()).isBlank()) {
            request.add(line);
        }
        return request;
    }

    private String readRequestBody(final BufferedReader bufferedReader, final Headers requestHeaders)
            throws IOException {
        if (!requestHeaders.contains(CONTENT_LENGTH)) {
            return "";
        }
        int contentLength = Integer.parseInt(requestHeaders.find(CONTENT_LENGTH));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }
}
