package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.controller.FrontController;

public class Http11Processor implements Runnable, Processor {
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final FrontController frontController = FrontController.getInstance();

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
                final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                final var outputStream = connection.getOutputStream()
        ) {
            HttpRequest request = getHttpRequest(bufferedReader);
            HttpResponse response = new HttpResponse();
            log.info("Requested endpoint: {}, Method: {}", request.getURI(), request.getHttpMethod());

            frontController.handle(request, response);

            outputStream.write(response.serialize().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest getHttpRequest(BufferedReader bufferedReader) throws IOException {
        RequestLine requestLine = RequestLine.from(bufferedReader.readLine());
        HttpHeaders headers = extractHeaders(bufferedReader);
        RequestBody requestBody = extractRequestBody(bufferedReader, headers);
        return new HttpRequest(requestLine, headers, requestBody);
    }

    private HttpHeaders extractHeaders(BufferedReader bufferedReader) throws IOException {
        List<String> headers = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            headers.add(line);
        }

        return HttpHeaders.from(headers);
    }

    public RequestBody extractRequestBody(BufferedReader bufferedReader, HttpHeaders headers) throws IOException {
        StringBuilder body = new StringBuilder();

        int contentLength;
        if (headers.haveContentLength() && (contentLength = headers.getContentLength()) > 0) {
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            body.append(buffer);
        }

        return new RequestBody(body.toString());
    }
}
