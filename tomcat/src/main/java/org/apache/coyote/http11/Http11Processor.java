package org.apache.coyote.http11;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.catalina.servlet.ServletManger;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final ServletManger servletManger;

    public Http11Processor(final Socket connection, final ServletManger servletManger) {
        this.connection = connection;
        this.servletManger = servletManger;
    }

    @Override
    public void run() {
        LOGGER.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream();
                final var reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            final var request = HttpRequest.from(reader);
            final var response = HttpResponse.createBasicResponseFrom(request);
            servletManger.createServlet().service(request, response);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
