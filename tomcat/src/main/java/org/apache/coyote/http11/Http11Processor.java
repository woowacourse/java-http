package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.FrontRequestHandler;
import org.apache.coyote.http11.message.HttpHeaders;
import org.apache.coyote.http11.message.HttpRequest;
import org.apache.coyote.http11.message.HttpResponse;
import org.apache.coyote.http11.message.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
             final var outputStream = connection.getOutputStream()) {
            FrontRequestHandler frontRequestHandler = new FrontRequestHandler();

            String startLine = bufferedReader.readLine();
            HttpHeaders httpHeaders = extractHeaders(bufferedReader);
            RequestBody requestBody = null;
            if (httpHeaders.containsKey("Content-Length")) {
                requestBody = extractRequestBody(Integer.parseInt(httpHeaders.get("Content-Length")), bufferedReader);
            }
            final HttpRequest httpRequest = HttpRequest.of(startLine, httpHeaders, requestBody);
            final ResponseEntity responseEntity = frontRequestHandler.handle(httpRequest);

            final HttpResponse httpResponse = HttpResponse.builder()
                    .httpStatus(responseEntity.getStatus())
                    .contentType(responseEntity.getContentType())
                    .body(responseEntity.getBody())
                    .headers(responseEntity.getHeaders())
                    .build();

            outputStream.write(httpResponse.getValue().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpHeaders extractHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();

        while (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            if (line.isEmpty()) {
                break;
            }
            String[] split = line.split(":");
            headers.put(split[0], split[1].trim());
        }
        return new HttpHeaders(headers);
    }

    private RequestBody extractRequestBody(int contentLength, BufferedReader bufferedReader) throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String request = new String(buffer);
        return RequestBody.of(request);
    }
}
