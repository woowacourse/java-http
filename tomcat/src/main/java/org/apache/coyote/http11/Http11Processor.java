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

            frontController.service(request, response);

            outputStream.write(response.serialize().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);

            sendErrorResponse(connection, 400, "Bad Request", "Invalid request format.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            sendErrorResponse(connection, 500, "Internal Server Error", "Unexpected error occurred.");
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

    private void sendErrorResponse(Socket connection, int statusCode, String statusMessage, String body) {
        try (var outputStream = connection.getOutputStream()) {
            String responseBody = String.format("<html><body><h1>%d %s</h1><p>%s</p></body></html>", statusCode, statusMessage, body);
            String errorResponse = String.format(
                    "HTTP/1.1 %d %s\r\nContent-Type: text/html\r\nContent-Length: %d\r\n\r\n%s",
                    statusCode, statusMessage, responseBody.getBytes(StandardCharsets.UTF_8).length, responseBody
            );
            outputStream.write(errorResponse.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException ioException) {
            log.error("Failed to send error response: {}", ioException.getMessage(), ioException);
        }
    }
}
