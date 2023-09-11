package org.apache.coyote.http11;

import org.apache.coyote.Processor;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.ControllerMapper;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

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
        try (final InputStream inputStream = connection.getInputStream();
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
             final OutputStream outputStream = connection.getOutputStream()) {

            final Request request = Request.from(bufferedReader);

            final Response response = new Response();

            response.preprocess(request);

            final Controller controller = ControllerMapper.getController(request);

            controller.service(request, response);

            response.postprocess(request);

            outputStream.write(response.parseString().getBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
