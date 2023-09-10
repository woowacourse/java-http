package org.apache.coyote.http11;


import org.apache.coyote.Controller;
import org.apache.coyote.Processor;
import org.apache.coyote.RequestMapping;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.HttpRequestBody;
import org.apache.coyote.request.HttpRequestHeader;
import org.apache.coyote.request.HttpRequestLine;
import org.apache.coyote.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

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
             final var outputStream = connection.getOutputStream();
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            HttpRequest request = generateHttpRequest(reader);

            HttpResponse response = HttpResponse.createDefaultResponse();
            Controller controller = new RequestMapping().getController(request);

            controller.service(request, response);

            outputStream.write(response.getResponse().getBytes());
            outputStream.flush();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest generateHttpRequest(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();

        HttpRequestLine httpRequestLine = HttpRequestLine.from(requestLine);
        HttpRequestHeader httpRequestHeader = getRequestHeader(reader);

        int contentLength = httpRequestHeader.getContentLength();
        HttpRequestBody httpRequestBody = getRequestBody(reader, contentLength);

        return new HttpRequest(httpRequestLine, httpRequestHeader, httpRequestBody);
    }

    private HttpRequestBody getRequestBody(BufferedReader reader, int contentLength) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        if (contentLength > 0) {
            char[] buffer = new char[contentLength];
            int bytesRead = reader.read(buffer, 0, contentLength);
            requestBody.append(buffer, 0, bytesRead);
        }

        return HttpRequestBody.from(requestBody.toString());
    }

    private HttpRequestHeader getRequestHeader(BufferedReader reader) throws IOException {
        StringBuilder requestHeader = new StringBuilder();

        String line;
        while (!Objects.equals(line = reader.readLine(), "")) {
            requestHeader.append(line).append("\r\n");
        }

        return HttpRequestHeader.from(requestHeader.toString());
    }
}
