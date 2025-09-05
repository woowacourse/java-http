package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import org.apache.catalina.connector.CoyoteAdepter;
import org.apache.coyote.Adapter;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.domain.ContentType;
import org.apache.coyote.http11.request.HttpRequestParser;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Adapter adapter;
    private final StaticResourceHandler staticResourceHandler;
    private final HttpRequestParser httpRequestParser;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.adapter = new CoyoteAdepter();
        this.staticResourceHandler = new StaticResourceHandler();
        this.httpRequestParser = new HttpRequestParser();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            final var httpRequest = httpRequestParser.getHttpRequest(bufferedReader);
            if (httpRequest == null) {
                return;
            }
            final String resourcePath = httpRequest.parseResourcePath();
            final var httpResponse = new Http11Response(resourcePath);

            adapter.service(httpRequest, httpResponse);

            writeResponse(httpResponse);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeResponse(Http11Response httpResponse) throws IOException, URISyntaxException {
        try (final var outputStream = connection.getOutputStream()) {
            setContentType(httpResponse.getResourcePath(), httpResponse);
            staticResourceHandler.handleStaticResource(httpResponse);
            outputStream.write(httpResponse.getResponseLine());
            outputStream.write(httpResponse.getHeader());
            outputStream.write(httpResponse.getBody());
            outputStream.flush();
        }
    }

    private static void setContentType(String resourcePath, Http11Response httpResponse) {
        final var contentType = ContentType.fromPath(resourcePath);
        httpResponse.addHeader("Content-Type", contentType.getValue());
    }
}
