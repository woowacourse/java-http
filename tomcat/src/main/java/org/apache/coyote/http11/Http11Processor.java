package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.FrontRequestHandler;
import org.apache.coyote.http11.http.HttpCookie;
import org.apache.coyote.http11.http.HttpHeaders;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.RequestBody;
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

            String startLine = bufferedReader.readLine();
            HttpHeaders httpHeaders = extractHeaders(bufferedReader);
            RequestBody requestBody = extractBody(bufferedReader, httpHeaders);
            HttpCookie httpCookie = extractCookie(httpHeaders);

            final HttpRequest httpRequest = HttpRequest.of(startLine, httpHeaders, requestBody, httpCookie);

            FrontRequestHandler frontRequestHandler = new FrontRequestHandler();
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

    private HttpCookie extractCookie(HttpHeaders httpHeaders) {
        HttpCookie httpCookie = new HttpCookie();
        if (httpHeaders.containsKey("Cookie")) {
            String cookieValue = httpHeaders.get("Cookie");
            Map<String, String> cookieValueMap = Arrays.stream(cookieValue.split("; "))
                    .map(it -> it.split("="))
                    .collect(Collectors.toMap(it -> it[0], it -> it[1]));

            httpCookie.putAll(cookieValueMap);
        }
        return httpCookie;
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

    private RequestBody extractBody(BufferedReader bufferedReader, HttpHeaders httpHeaders) throws IOException {
        RequestBody requestBody = new RequestBody();
        if (httpHeaders.containsKey("Content-Length")) {
            requestBody = extractRequestBody(Integer.parseInt(httpHeaders.get("Content-Length")), bufferedReader);
        }
        return requestBody;
    }

    private RequestBody extractRequestBody(int contentLength, BufferedReader bufferedReader) throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String request = new String(buffer);
        return RequestBody.of(request);
    }
}
