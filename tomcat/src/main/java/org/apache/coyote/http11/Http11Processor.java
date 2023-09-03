package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.catalina.servlet.Servlet;
import org.apache.catalina.servlet.request.HttpRequest;
import org.apache.catalina.servlet.response.HttpResponse;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final HttpRequestParser parser = new HttpRequestParser();

    private final Servlet dispatcherServlet;
    private final Socket connection;

    public Http11Processor(Servlet dispatcherServlet, final Socket connection) {
        this.dispatcherServlet = dispatcherServlet;
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (var bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             var bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {
            HttpRequest request = parser.parse(bufferedReader);
            HttpResponse response = dispatcherServlet.service(request);
            bufferedWriter.write(response.toString());
            bufferedWriter.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
