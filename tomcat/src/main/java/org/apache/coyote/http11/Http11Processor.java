package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.url.HandlerMapping;
import org.apache.coyote.http11.url.Url;
import org.apache.coyote.http11.utils.UrlParser;
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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            String request = bufferedReader.readLine();

            String uri = UrlParser.extractUri(request);
            HttpMethod httpMethod = UrlParser.extractMethod(request);
            HttpHeaders httpHeaders = HttpHeaders.create(bufferedReader);

            String requestBody = extractRequestBody(bufferedReader, httpHeaders, httpMethod);

            Url url = HandlerMapping.from(uri, new Http11Request(httpHeaders, httpMethod));

            Http11Response resource = url.handle(httpHeaders, requestBody);
            String response = resource.toResponse();
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }


    private String extractRequestBody(BufferedReader bufferedReader, HttpHeaders httpHeaders, HttpMethod httpMethod)
            throws IOException {
        String requestBody = "";
        if (httpMethod.equals(HttpMethod.POST)) {
            int contentLength = Integer.parseInt(httpHeaders.get("Content-Length"));
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            requestBody = new String(buffer);
        }
        log.info("requestBody : {}", requestBody);
        return requestBody;
    }

}
