package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import org.apache.catalina.servlets.http.request.HttpRequest;
import org.apache.catalina.servlets.http.response.HttpResponse;
import org.apache.catalina.mapper.Mapper;
import org.apache.catalina.servlets.Servlet;
import org.apache.coyote.Processor;
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
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int read = inputStream.read(buffer);
            HttpRequest request = new HttpRequest(new String(buffer, 0, read));

            Servlet servlet = Mapper.getInstance().getServlet(request.getRequestURI());

            HttpResponse response = new HttpResponse(outputStream);
            servlet.service(request, response);

            PrintWriter writer = response.getWriter();
            response.write();
            writer.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
