package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.Processor;
import org.apache.http.request.HttpRequestParser;
import org.apache.http.request.HttpTomcatRequest;
import org.apache.http.response.HttpResponseParser;
import org.apache.http.response.HttpTomcatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    private final HttpRequestParser httpRequestParser = new HttpRequestParser();
    private final HttpResponseParser httpResponseParser = new HttpResponseParser();

    private final ResourceMappingProvider resourceMappingProvider = new ResourceMappingProvider();

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

            System.out.println("inputStream = " + new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));

            final HttpTomcatRequest httpTomcatRequest =
                    httpRequestParser.parse(
                            HttpTomcatRequest.class,
                            new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));




            final HttpTomcatResponse httpTomcatResponse = HttpTomcatResponse.createDefault();
            final String response = httpResponseParser.parse(httpTomcatResponse);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
