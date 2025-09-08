package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.servlet.Servlet;
import org.apache.catalina.servlet.ServletContainer;
import org.apache.coyote.Processor;
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

    //TODO: 헤더와 바디도 읽도록 RequestParser/Reader 구현 필요
    // https://github.com/woowacourse/java-http/pull/800#discussion_r2321263463  (2025-09-7, 일, 17:18)
    @Override
    public void process(final Socket connection) {
        try (
                connection;
                var reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.ISO_8859_1));
                var writer = connection.getOutputStream()
        ) {
            String requestLine = reader.readLine();
            if (requestLine == null || requestLine.isBlank()) {
                return;
            }

            HttpRequest request = HttpRequest.from(requestLine);
            HttpResponse response = new HttpResponse();
            Servlet servlet = servletContainer.getServletBy(request.getRequestPath());
            servlet.service(request, response);
            response.writeTo(writer);
            writer.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
