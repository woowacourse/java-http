package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.servlet.ServletContainer;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.message.parser.HttpHeadersParser;
import org.apache.coyote.http11.message.parser.RequestLineParser;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final ServletContainer servletContainer;

    public Http11Processor(Socket connection, ServletContainer servletContainer) {
        this.connection = connection;
        this.servletContainer = servletContainer;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (
                connection;
                var reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                var writer = connection.getOutputStream()
        ) {
            HttpRequest request = HttpRequest.from(reader, new RequestLineParser(), new HttpHeadersParser());
            HttpResponse response = new HttpResponse();

            servletContainer.executeServlet(request, response);

            response.writeTo(writer);
            writer.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
